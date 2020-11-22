# Exam application


##Docker

### Google Cloud SDK må være installert


Aktiver Google Container Registry for prosjektet.

Det må lages en Service Account som Travis-CI kan bruke til å pushe image til Google Container Registry.
1. Gi Service Accounten en rolle som Storage Admin.
2. Last ned en .json-nøkkel som Storage Admin kan bruke.
Nøkkelen kan få navnet exam1pg301 og plasses i root i prosjektet.
Krypter nøkkelen med kommandoen:
####travis encypt-file --pro [nøkkelnavn].json --add

Deretter kopier melding som kommer opp under ("openssl aes-256-cbc ...")
inn i i travis.yml (erstatt den som er der).

(I travis.yml:)
#####gcloud auth activate-service-account --key-file=[nøkkelnavn].json

Krypter prosjektID:
####travis encrypt --pro GCP_PROJECT_ID="[GCP Prosjektets ID]" --add
Deretter limes en "secure ..." inn. (Denne må erstattes med den som allerede er der)


##Metrics
####Start influx:
` docker run --name influxdb \
    -p 8083:8083 -p 8086:8086 -p 25826:25826/udp \
    -v $PWD/influxdb:/var/lib/influxdb \
    -v $PWD/influxdb.conf:/etc/influxdb/influxdb.conf:ro \
    -v $PWD/types.db:/usr/share/collectd/types.db:ro \
    influxdb:1.0`
    
 #####Start Grafana med docker:
 
 docker run -d -p 3000:3000 --name grafana grafana/grafana:6.5.0`
 hvis dere går til http://localhost:3000/ får dere opp et enkelt brukergrensesnitt. - I grafana, Konfigurer en datasource og bruk følgende verdi som URL
 
 `http://host.docker.internal:8086`
 
 Velg database "mydb". Resten av verdiene kan være uendret.
 
 
 (Har hatt litt problemer med influxDB lokalt, så har ikke fått sjekket selv)
 
 ##Logger
 
 Få tak i egen token (LOGZ_TOKEN) og url (LOGZ_URL):
   Gå inn på Logz.io 
         -> Send your data 
             -> Libraries -
                 > Java - logback appender
 
 Kryper token og url:
 ´travis encrypt --pro LOGZ_TOKEN=<Logz.io token til bruker> --add´
 ´travis encrypt --pro LOGZ_URL=<Logz.io URL for Logz.io> --add´
 
 
 ##--> push til master 
 
        