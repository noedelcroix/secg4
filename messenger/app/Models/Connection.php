<?php
namespace App\Models;

use Illuminate\Support\Str;

class Connection{
    public static function auth($login,$pswd){
        $checkPswd = \DB::select('select password,id from users where login = ?',[$login]);
        if (password_verify($pswd, $checkPswd[0]->password)) {
            $token = (String) Str::uuid();
            \DB::update('UPDATE users SET token = ? where id = ?', [$token,$checkPswd[0]->id]);
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
        \DB::update('UPDATE users SET token = NULL where id = ?', [$user[0]->id]);
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
    public static function stockPublicKey($token, $publickey){
        $user = \DB::select('select id from users where token = ?',[$token]);
        if(count($user)==0){
            throw new Exception('no token found');
        }

        $foundPublic = \DB::select('select * from user_keys where id = ?',[$user[0]->id]);

        if(count($foundPublic)==0){
        \DB::insert("INSERT INTO `user_keys`(id,public_key) VALUES(?,?)",[$user[0]->id, $publickey]);
        }
    }
    public static function getKey($login){
        $user = \DB::select('select id from users where login = ?',[$login]);
        if(count($user)==0){
            throw new Exception('no token found');
        }
        $key = \DB::select('select public_key from user_keys where id = ?',[$user[0]->id]);
        return $key;
    }

}