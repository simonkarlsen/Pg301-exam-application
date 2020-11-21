package no.kristiania.exam.user;

import no.kristiania.exam.post.Post;
import no.kristiania.exam.exception.UserNotFoundException;
import no.kristiania.exam.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserJPAController {

    private static final Logger LOG = Logger.getLogger(UserJPAController.class.getName());

    @Autowired
    PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public List<User> home() {
        LOG.info("home ('/')");
        return userRepository.findAll();
    }

    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        LOG.info("/users");
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            LOG.info("/users/" + id + " not found!");
            throw new UserNotFoundException("id:" + id);
        }

        EntityModel<User> resource = new EntityModel<User>(user.get());
        Link link = linkTo(methodOn(UserJPAController.class).retrieveAllUsers()).withSelfRel();
        resource.add(link.withRel("all-users"));

        LOG.info("/users/" + id);

//        assertThat(link.getHref()).endsWith("/people/2");

        return resource;
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {

        User savedUser = userRepository.save(user);

        // user/{id} --> /user/savedUser.getId()
        URI location = ServletUriComponentsBuilder.
                fromCurrentRequest().path("/{id}").
                buildAndExpand(savedUser.getId()).toUri();

        LOG.info("(POST-->) /users \n new user: /users/"+ savedUser.getId());

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id) {
        userRepository.deleteById(id);

        LOG.info("(DELETE-->) jpa/users/" + id);

        // nothing returned --> success
    }

    @GetMapping("/users/{id}/posts")
    public List<Post> retrieveAllUserPosts(@PathVariable int id) {
        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isEmpty()) {
            throw new UserNotFoundException("id:" + id );
        }

        return userOptional.get().getPosts();
    }


    @PostMapping("/users/{id}/posts")
    public ResponseEntity<Object> createPost(@PathVariable int id,
                                             @RequestBody Post post) {

       Optional<User> userOptional = userRepository.findById(id);

       if(userOptional.isEmpty()) {
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

        return ResponseEntity.created(location).build();
    }
}
