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

Route::get('{token}/deconnect',[ConnectionController::class,'deconnect']);

Route::get('/{token}/chat/{user}',[MessageController::class,'getMessages']);

Route::post('/{token}/chat/{user}',[MessageController::class,'postMessage']);

Route::get('/{token}/friends/',[FriendsController::class,'getFriends']);

Route::get('/{token}/addfriend/',[FriendsController::class,'addFriend']);

Route::get('/{token}/delfriend/',[FriendsController::class,'delFriend']);

Route::get('/{token}/getonlinefriends/',[FriendsController::class,'getOnlineFriends']);


