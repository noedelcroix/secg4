<?php
namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Connection;

class ConnectionController{

    public static function auth(Request $request){
        $login = $request->post("login");
        $pswd = $request->post("pswd");
        $response =Connection::auth($login,$pswd);
        return response()->json($response);
    }
    public static function newUser(Request $request){
        $login = $request->post('login');
        $pswd = $request->post("pswd");
        $response =null;
        try {
            $response = Connection::newUser($login,$pswd);
        } catch (Exception $e) {
            return response()->json(false, 500);
        }
        return response()->json($response);
    }
    public static function deconnect($token){
        try {
            Connection::deconnect($token);
        } catch (Exception $e) {
            return response()->json(false, 500);
        }
        return response()->json(true, 201);

    }

}