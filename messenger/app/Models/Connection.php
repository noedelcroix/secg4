<?php
namespace App\Models;

use Illuminate\Support\Str;

class Connection{
    public static function auth($login,$pswd){
        $checkPswd = \DB::select('select password from users where login = ?',[$login]);
        if (password_verify($pswd, $checkPswd[0]->password)) {
            $token = (String) Str::uuid();
            $user = \DB::select('select id from users where login = ?',[$login]);
            \DB::update('UPDATE users SET token = ? where id = ?', [$token,$user[0]->id]);
            return $token;
        } else {
            throw new Exception('auth failure');
        }
    }
    public static function deconnect($token){
        $user = \DB::select('select id from users where token = ?',[$token]);
        if(count($user)==0){
            throw new Exception('no token found');
        }
        \DB::update('UPDATE users SET token = NULL where id = ?'[$user[0]->id]);
    }
    public static function newUser($login, $pwd){
        /**
         * cout algorithmique
         */
        $options = [
            'cost' => 11,
        ];
        $hash = password_hash($pwd, PASSWORD_BCRYPT, $options); // pcq le salt précisé explicitement est ignoré :))
        $result = \DB::insert("INSERT INTO `users`(login,password) VALUES(?,?)",[$login, $hash]);

    }

}