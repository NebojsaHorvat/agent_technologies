'use strict';

angular.module('core.wsService')
	.service('wsService', function($http,$window,$rootScope,$state) {
		
		var url = $window.location;
		var host = "ws://" + url.hostname + ":" + url.port + "/agentWeb/Socket";
		
		try {
			   this.socket = new WebSocket(host);
			  // $rootScope.socket = this.socket;
			   
			   this.socket.onopen = function() {
				   console.log("Socket connection opened");
			   }
			   
			   this.socket.onmessage = function(message) {
				   
				  
				   this.payload = JSON.parse(message.data);
				   this.contentObjest = JSON.parse(this.payload.content);
				   
				   if(this.payload.type == 'AGENT_CLASSES_REMOVAL'){
					   $rootScope.$broadcast('agentClassesForRemoval',this.contentObjest );
				   }
				   else if(this.payload.type == 'AGENT_CLASSES'){
					   $rootScope.$broadcast('agentClasses',this.contentObjest );
				   }
				   else if(this.payload.type == 'MESSAGE'){
					   $rootScope.$broadcast('MESSAGE',this.contentObjest );
				   }else if(this.payload.type == 'LOGOUT'){
					   $rootScope.user = null;
					   $state.go('home.chat');
				   }else if(this.payload.type == 'GROUPS'){
					   // TODO
				   }
				   else if(this.payload.type == 'USER_FRIENDS_RES') {
					   $rootScope.$broadcast(this.payload.type, this.contentObjest);
				   }
				   
				   alert(message.data);
			   }
			   
			   socket.onclose = function() {
				   this.socket = null;
				   console.log("socket connection closed");
			   }
		   } catch(exception) {
			   console.log("Error!");
		   }
		   
		   
	   this.login = (user) => {
		   this.message ={
			"type" : "LOGIN",
			"content" : JSON.stringify(user)
		   };
		   
			if(this.socket != null){
				this.socket.send(JSON.stringify(this.message));
			}
		};
		
		this.register = (user) => {
			this.message ={
					"type" : "REGISTER",
					"content" : JSON.stringify(user)
				   };
				   
					if(this.socket != null){
						this.socket.send(JSON.stringify(this.message));
					}
		};
		
		 this.getAgentClasses = () => {

			 this.message ={
						"type" : "AGENT_CLASSES",
						"content" : ""
					   };
				if(this.socket != null){
					this.socket.send(JSON.stringify(this.message));
				}
		};
		
		   
		 this.sendMessage = (currentChat,content) => {

			 	this.date = new Date();
			 	this.currentTimeStamp = this.date.getTime()/1000;
				if(this.socket != null){
					
					this.contentMessage ={
							chat: currentChat,
							content: content,
							sender : "",
							timeStamp: this.currentTimeStamp
					};
					 this.message ={
								"type" : "MESSAGE",
								"content" : JSON.stringify(this.contentMessage)
					};
					 
					this.socket.send(JSON.stringify(this.message));
				}
			};
			
			this.logout = () => {
				this.message ={
						"type" : "LOGOUT"
					   };
					   
						if(this.socket != null){
							this.socket.send(JSON.stringify(this.message));
						}
			}

			this.getGroups = () => {
				this.message ={
						"type" : "GROUPS"
					   };
					   
						if(this.socket != null){
							this.socket.send(JSON.stringify(this.message));
						}
			}

		this.sendFriendReqMsg = (content) => {
			this.message ={
					"type" : "USER_FRIENDS_REQ",
					"content" : JSON.stringify(content)
			};
			this.socket.send(JSON.stringify(this.message));
		};

	});