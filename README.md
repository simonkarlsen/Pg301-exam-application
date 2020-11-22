# Exam application


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