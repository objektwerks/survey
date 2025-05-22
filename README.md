Survey
------
>Survey service using Scala 3.

Model
-----
* Account 1 --- * Survey
* Survey 1 --- * Participant
* Survey 1 --- * Question
* Question 1 --- 1 Answer
>Questions: Choices, Ranking, Rating, Text

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

Resources
---------
* [Survey Monkey](https://www.surveymonkey.com/mp/survey-question-types/)

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