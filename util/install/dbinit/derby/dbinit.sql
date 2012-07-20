connect 'jdbc:derby://localhost:1527/yes;create=true';
run '../../../persistence/sql/resources/derby/create-tables.sql';
run '../../../util/install/dbinit/initdata.sql';
disconnect;
connect 'jdbc:derby://localhost:1527/yespay;create=true';
run '../../../core-modules/core-module-payment-base/src/main/resources/sql/derby/create-tables.sql';
run '../../../core-modules/core-module-payment-base/src/main/resources/sql/payinitdata.sql';
run '../../../core-modules/core-module-payment-capp/src/main/resources/sql/payinitdata.sql';
run '../../../core-modules/core-module-payment-gcwm/src/main/resources/sql/payinitdata.sql';
disconnect;
