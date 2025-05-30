Survey
------
>Survey http service using Jsoniter, ScalikeJdbc, Scaffeine, HikariCP, Tapir, Ox, Postgresql and Scala 3.

Model
-----
* Account 1 --- * Survey
* Survey 1 --- * Question
* Survey | Question | Participant 1 --- * Answer

Install
-------
1. brew install postgresql@14

Build
-----
1. sbt clean compile

Test
----
1. sbt clean test

Run
---
1. sbt run
>Output:
```
*** Survey Http Server started at: 127.0.0.1:7070/command
*** Survey Command Endpoint: POST /command {body as application/json (UTF-8)} -> -/{body as application/json (UTF-8)}
*** Survey Swagger Endpoint: GET /docs /docs.yaml -> -/{body as application/yaml (UTF-8)}
*** Press Control-C to shutdown Survey Http Server at: 127.0.0.1:7070/command
^C
[warn] Canceling execution...
*** Survey Http Server shutdown at: 127.0.0.1:7070
```

Curl
----
>Request:
```
curl -X POST http://127.0.0.1:7070/command -H 'Content-Type: application/json' -d '{"Register": {"email":"your-email@provider.com"}}'
```
>Response:
```
{"Registered":{"account":{"id":3,"license":"b1e86698-85bb-45b2-afec-0520d7dd3c3c","email":"your-email@provider.com","pin":"yS#eX6="}}}%  
```

Postgresql
----------
1. config:
    1. on osx intel: /usr/local/var/postgres/postgresql.config : listen_addresses = ‘localhost’, port = 5432
    2. on osx m1: /opt/homebrew/var/postgres/postgresql.config : listen_addresses = ‘localhost’, port = 5432
2. run:
    1. brew services start postgresql@14
3. logs:
    1. on osx intel: /usr/local/var/log/postgres.log
    2. on m1: /opt/homebrew/var/log/postgres.log

Database
--------
>Example database url: postgresql://localhost:5432/survey?user=yourcomputername&password=survey"
1. psql postgres
2. CREATE DATABASE survey OWNER [your computer name];
3. GRANT ALL PRIVILEGES ON DATABASE survey TO [your computer name];
4. \l
5. \q
6. psql survey
7. \i ddl.sql
8. \q

DDL
---
>Alternatively run: psql -d survey -f ddl.sql
1. psql survey
2. \i ddl.sql
3. \q

Drop
----
1. psql postgres
2. drop database survey;
3. \q

Environment
-----------
>The following environment variables must be defined:
```
export SURVEY_HOST="127.0.0.1"
export SURVEY_PORT=7070
export SURVEY_ENDPOINT="/command"
export SURVEY_PATH="command"

export SURVEY_CACHE_INITIAL_SIZE=4
export SURVEY_CACHE_MAX_SIZE=10
export SURVEY_CACHE_EXPIRE_AFTER=24

export SURVEY_POSTGRESQL_DRIVER="org.postgresql.ds.PGSimpleDataSource"
export SURVEY_POSTGRESQL_URL="jdbc:postgresql://localhost:5432/survey"
export SURVEY_POSTGRESQL_USER="your.computer.name"
export SURVEY_POSTGRESQL_PASSWORD="your.password"

export SURVEY_EMAIL_HOST="your.email.host"
export SURVEY_EMAIL_ADDRESS="your.email.address@email.com"
export SURVEY_EMAIL_PASSWORD="your.email.password"
```

Resources
---------
* [Survey Monkey](https://www.surveymonkey.com/mp/survey-question-types/)
* [Tapir](https://tapir.softwaremill.com/en/latest/)

License
-------
>Copyright (c) 2025 Objektwerks

>Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    * http://www.apache.org/licenses/LICENSE-2.0

>Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.