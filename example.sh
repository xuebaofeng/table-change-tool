#!/usr/bin/env bash

git pull

gradle fatJar

cp build/libs/g-1.0.jar g.jar

#replace table name $ with _
java -jar g.jar -table lc_addr -cp /Users/bxue/tlc/lc-common -mp /Users/bxue/tlc/main -col city,street,street_no,zip, -jpa LcAddress -username "Baofeng Xue" -ticket 63428
