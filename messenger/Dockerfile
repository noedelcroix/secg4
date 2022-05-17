FROM python:latest

RUN mkdir /messenger/
WORKDIR /messenger/

RUN apt-get update -y && apt-get upgrade -y
RUN apt-get install zip unzip sqlite3 lsb-release -y

#Install php 8
RUN echo "deb https://packages.sury.org/php/ $(lsb_release -sc) main" | tee /etc/apt/sources.list.d/sury-php.list
RUN wget -qO - https://packages.sury.org/php/apt.gpg | apt-key add -

#Install Node JS
RUN curl -sL https://deb.nodesource.com/setup_17.x | bash -

RUN apt-get update -y
RUN apt-get install php8.1 phpunit php-curl php-sqlite3 apache2 nodejs -y

#Install composer
RUN php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');" && \
php -r "if (hash_file('sha384', 'composer-setup.php') === '55ce33d7678c5a611085589f1f3ddf8b3c52d662cd01d4ba75c0ee0459970c2200a51f492d557530c71c15d8dba01eae') { echo 'Installer verified'; } else { echo 'Installer corrupt'; unlink('composer-setup.php'); } echo PHP_EOL;" && \
php composer-setup.php --install-dir=/usr/local/bin --filename=composer &&\
php -r "unlink('composer-setup.php');"

#Create certificate
RUN mkdir -p /etc/ssl/certs /etc/ssl/private
RUN echo 'openssl req -x509 -out /etc/ssl/certs/localhost.crt -keyout /etc/ssl/private/localhost.key \
  -newkey rsa:2048 -nodes -sha256 \
  -subj "/CN=localhost" -extensions EXT -config <( \
   printf "[dn]\nCN=localhost\n[req]\ndistinguished_name = dn\n[EXT]\nsubjectAltName=DNS:localhost\nkeyUsage=digitalSignature\nextendedKeyUsage=serverAuth")' >script.sh
RUN bash ./script.sh
RUN rm -f ./script.sh
RUN chmod 440 /etc/ssl/private/localhost.key

#Config Apache
RUN echo "<VirtualHost *:80>\nDocumentRoot /messenger/public\n<Directory /messenger/public/>\nOptions Indexes FollowSymLinks MultiViews\nAllowOverride All\nOrder allow,deny\nallow from all\nRequire all granted\n</Directory>\n</VirtualHost>" > /etc/apache2/sites-available/messenger.conf
RUN echo "<VirtualHost *:443>\nDocumentRoot /messenger/public\n<Directory /messenger/public/>\nOptions Indexes FollowSymLinks MultiViews\nAllowOverride All\nOrder allow,deny\nallow from all\nRequire all granted\n</Directory>\nServerSignature Off\nSSLEngine on\nSSLCertificateFile /etc/ssl/certs/localhost.crt\nSSLCertificateKeyFile /etc/ssl/private/localhost.key\n</VirtualHost>" >> /etc/apache2/sites-available/messenger.conf

#Install messenger
COPY . .

RUN chown -R www-data:www-data /messenger

RUN chown -R www-data:www-data /messenger

RUN php /usr/local/bin/composer install && npm install
RUN if  ! test -f database/database.sqlite; then touch database/database.sqlite && php artisan migrate:refresh; fi
RUN npm run dev

RUN chown www-data:www-data /messenger/database/database.sqlite

RUN a2dissite 000-default
RUN a2enmod rewrite
RUN a2enmod ssl
RUN a2ensite messenger

#CMD ["service", "apache2", "start"]
CMD [ "apachectl", "-D", "FOREGROUND" ]

EXPOSE 80 443