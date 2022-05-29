# Messenger
## Run production
```bash
docker build . -t messenger
#You can use shared database between host and docker container like this : docker run -dp 80:80 -p 443:443 --mount type=bind,source="$(pwd)"/database,target=/messenger/database -t messenger
docker run -d -p 443:443 -t messenger
```

## Run dev
```bash
chmod 777 -R storage
composer install && npm install
if  ! test -f database/database.sqlite; then
touch database/database.sqlite
php artisan migrate:refresh
fi
npm run dev
php artisan serve
```