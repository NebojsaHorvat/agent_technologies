'use strict';

angular.module('agent_list')
	.component('myAgentList', {
		templateUrl: 'part/agent_list/agent_list.template.html',
		controller: function( $rootScope, $state,wsService,$scope,$element) {
			
			wsService.getAgentClasses();
			
			$rootScope.agentClasses = ['a','b','c'];
			
			$scope.$on('agentClasses', function (event, arg) { 
				
				$rootScope.agentClasses = arg.filter(function(el){
					return !el.includes("Local");
				});
				//$rootScope.agentClasses = arg;
				$scope.$apply();
			  });
			
			this.send = () =>{
//				wsService.sendMessage(this.currentChat,this.content);
//				$element.find('textarea').val($element.find('textarea').val() +$rootScope.user.username+" : "+ this.content + "\n");
			}
		}
	});		