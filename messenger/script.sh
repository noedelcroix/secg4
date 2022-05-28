
chmod 777 -R storage
composer install && npm install
if  ! test -f database/database.sqlite; then
touch database/database.sqlite
php artisan migrate:refresh
fi
npm run dev
php artisan serve
