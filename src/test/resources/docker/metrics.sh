REM Check MySQL status
docker exec -it jsql-mysql /bin/bash  \
  -c "                                \
     mysql -uroot -pmy-secret-pw -e ' \
     SHOW GLOBAL status WHERE Variable_name RLIKE \"Threads_cached|Max_used_connections|Threads_created|Connections|Opened_tables|Threads_connected|Threads_running|^Queries\" \
     ';                               \
     "