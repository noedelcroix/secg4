<?php

use Illuminate\Http\Request;

use Illuminate\Support\Facades\Route;

use App\Http\Controllers\ConnectionController;
use App\Http\Controllers\FriendsController;
use App\Http\Controllers\MessageController;
use App\Http\Controllers\ChatController;


Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

Route::post('/auth',[ConnectionController::class,'auth']);

Route::post('/createaccount',[ConnectionController::class,'newUser']);

Route::get('/deconnect/{token}',[ConnectionController::class,'deconnect']);

Route::get('/chat/{user}/{token}',[MessageController::class,'getMessages']);

Route::post('/chat/{user}/{token}',[MessageController::class,'postMessage']);

Route::get('/addfriend/{user}/{token}',[FriendsController::class,'addFriend']);

Route::get('/getonlinefriends/{token}',[FriendsController::class,'getOnlineFriends']);

Route::get('/getfriends/{token}',[FriendsController::class,'getFriends']);

Route::get('/getpendinginvitations/{token}',[FriendsController::class,'getPendingInvitations']);

Route::get('/publickey/{login}',[ConnectionController::class,'getKey']);

Route::post('/publickey',[ConnectionController::class,'stockPublicKey']);