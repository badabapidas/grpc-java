@echo off
cls
del /f ssl
mkdir ssl
 

SET SERVER_CN=localhost
SET CERTPATH=ssl

rem Step 1: Generate Certificate Authority + Trust Certificate (ca.crt)
openssl genrsa -passout pass:1111 -des3 -out %CERTPATH%/ca.key 4096
openssl req -passin pass:1111 -new -x509 -days 365 -key %CERTPATH%/ca.key -out %CERTPATH%/ca.crt -subj "/CN=%SERVER_CN%"

rem Step 2: Generate the Server Private Key (server.key)
openssl genrsa -passout pass:1111 -des3 -out %CERTPATH%/server.key 4096

rem Step 3: Get a certificate signing request from the CA (server.csr)
openssl req -passin pass:1111 -new -key %CERTPATH%/server.key -out %CERTPATH%/server.csr -subj "/CN=%SERVER_CN%"

rem Step 4: Sign the certificate with the CA we created (it's called self signing) - server.crt
openssl x509 -req -passin pass:1111 -days 365 -in %CERTPATH%/server.csr -CA %CERTPATH%/ca.crt -CAkey %CERTPATH%/ca.key -set_serial 01 -out %CERTPATH%/server.crt 

rem Step 5: Convert the server certificate to .pem format (server.pem) - usable by gRPC
openssl pkcs8 -topk8 -nocrypt -passin pass:1111 -in %CERTPATH%/server.key -out %CERTPATH%/server.pem