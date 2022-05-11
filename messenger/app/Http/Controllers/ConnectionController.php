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

}