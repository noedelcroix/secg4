# Messenger
## Run production
```bash
docker build . -t messenger
docker run -dp 80:80 -p 443:443 messenger
```

## Run dev
```bash
chmod 777 -R storage
php composer install && npm install
if  ! test -f database/database.sqlite; then
touch database/database.sqlite
php artisan migrate:refresh
fi
npm run dev
php artisan serve
```

## TODO
no Replayable message