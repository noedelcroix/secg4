<?php

namespace App\Http\Controllers;
use App\Models\Message;
use Illuminate\Http\Request;

class MessageController{

    public static function getConversations($token){
        return Message::getConversations($token);
    }    
    public static function getMessages($token,$user){
        return Message::getMessages($token,$user);
    }
    public static function postMessage($token, $user, Request $request){
        $token = $request->post("token");
        return Message::postMessage($token,$user);
    }

}