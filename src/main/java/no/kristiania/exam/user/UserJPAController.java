package no.kristiania.exam.user;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.Api;
import no.kristiania.exam.post.Post;
import no.kristiania.exam.exception.UserNotFoundException;
import no.kristiania.exam.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Api(value = "/")
@RestController
public class UserJPAController {

    private static final Logger LOG = Logger.getLogger(UserJPAController.class.getName());

    @Autowired
    PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private MeterRegistry meterRegistry;

    @Autowired
    private UserService userService;


    @Autowired
    public UserJPAController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }


    // just a default home screen
    @GetMapping("/")
    public String home() {
        LOG.info("home-function called ('/')");

        return "HOME\n* Go to /users to see all users.\n* Go to /users/{id} (eks. 10001) to see a " +
                "specific user.\n* G to /users/{id}/posts to see posts of users \n" +
                "*To add a new user, send a POST request to /users:" +
                "{'name': 'Per', 'birthDate': '2010-11-21T23:00:00.000+00:00'}\n" +
                "(Change singlequotes to doubblequotes)\n" +
                "* To delete a user, send a DELETE request to /users/{id}\n" +
                "* To make a new post, send a POST request to /users/{id}/posts";
    }


    @Timed(description = "Time usage of retrieving all users", value = "user.time.retrieveAllUsers")
    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        LOG.info("retrieveAllUsers-function called (/users)");

        DistributionSummary
                .builder("user.retrieveAllUsers.users")
                .baseUnit("nr")
                .register(meterRegistry)
                .record(userRepository.findAll().size());

        Gauge
                .builder("user.amount.users", userService, userService::getUserCount)
                .register(meterRegistry);
        LOG.info("User count: " + userService.getUserCountToString(userService.getUserCount(userService)));

        return userRepository.findAll();
    }

    @Timed(description = "Time usage of retrieving user", value = "user.time.retrieveUser")
    @GetMapping("/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            LOG.warning("/users/" + id + " not found!");

            meterRegistry.counter("user.count.retrieveUser", "result", Result.FAILURE.toString()).increment();
            LOG.warning(Result.FAILURE.toString());

            throw new UserNotFoundException("id:" + id);
        }

        EntityModel<User> resource = new EntityModel<User>(user.get());
        Link link = linkTo(methodOn(UserJPAController.class).retrieveAllUsers()).withSelfRel();
        resource.add(link.withRel("all-users"));



        meterRegistry.counter("user.count.retrieveUser", "result", Result.SUCCESS.toString()).increment();
        LOG.info("retrieveUser() called (/users/" + id);

//        assertThat(link.getHref()).endsWith("/people/2");

        return resource;
    }

    @Timed(description = "Time usage of creating user", value = "user.time.createUser")
    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {

        User savedUser = userRepository.save(user);

        // user/{id} --> /user/savedUser.getId()
        URI location = ServletUriComponentsBuilder.
                fromCurrentRequest().path("/{id}").
                buildAndExpand(savedUser.getId()).toUri();

        LOG.info("createUser() called. (POST-->) /users \n new user:" + savedUser.getName()
                + " --> /users/"+ savedUser.getId());

        meterRegistry.counter("user.count.createUser", "result", Result.SUCCESS.toString()).increment();



        return ResponseEntity.created(location).build();
    }

    @Timed(description = "Time usage of deleting user", value = "user.time.deleteUser")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id) {
        userRepository.deleteById(id);

        LOG.info("deleteUser() called (DELETE-->) jpa/users/" + id);

        meterRegistry.counter("user.count.deleteUser", "result", Result.SUCCESS.toString()).increment();

    }


    @GetMapping("/users/{id}/posts")
    public List<Post> retrieveAllUserPosts(@PathVariable int id) {
        LOG.info("retrieveAllUserPosts called with id: " + id);
        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isEmpty()) {
            LOG.warning("retrieveAllUserPosts. No posts found. User with id" + id + " does not exist.");
            throw new UserNotFoundException("id:" + id );
        }

        meterRegistry.counter("user.count.retrieveAllUserPosts", "result", Result.SUCCESS.toString()).increment();

        return userOptional.get().getPosts();
    }



    @Timed(description = "Time usage of creating a post", value = "user.time.createPost", longTask = true)
    @PostMapping("/users/{id}/posts")
    public ResponseEntity<Object> createPost(@PathVariable int id,
                                             @RequestBody Post post) {


        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isEmpty()) {
            LOG.warning("createPost. User with id" + id + " does not exist.");
            throw new UserNotFoundException("id:" + id);
        }

        User user = userOptional.get();

        post.setUser(user);

        postRepository.save(post);

        // user/{id} --> /user/savedUser.getId()
        URI location = ServletUriComponentsBuilder.
                fromCurrentRequest().path("/{id}").
                buildAndExpand(post.getId()).toUri();

        LOG.info("(POST-->) /jpa/users/{id}/posts\n new post: /jpa/users/{id}/posts/"
                + post.getId());

        LOG.info("post:" + post.getDescription()
                + ", by user" + user.getName());

        meterRegistry.counter("user.count.createPost", "result", Result.SUCCESS.toString()).increment();

        return ResponseEntity.created(location).build();
    }

    enum Result {
        SUCCESS,
        FAILURE
    }
}
