git pull
call gradle fatJar
copy build\libs\g-1.0.jar g.jar
call java -jar g.jar -table lc$addr -cp z:/lc-common -mp z:/main -col city,zip,street,street_no -jpa LcAddress -username "Baofeng Xue" -ticket 66666
