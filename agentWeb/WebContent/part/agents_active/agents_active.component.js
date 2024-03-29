'use strict';

angular.module('agents_active')
	.component('myAgentsActive', {
		templateUrl: 'part/agents_active/agents_active.template.html',
		controller: function( $rootScope, $state,wsService,$scope,$element,agentService) {
			
			agentService.getActiveAgents()
			.then( (response) => {
				$rootScope.activeAgents = response.data.runningAgents;
			}, () => {
				$rootScope.activeAgents = null;
			});
			 		
			$scope.$on('activeAgent', function (event, arg) { 
				
				$rootScope.activeAgents.push(arg);
				$scope.$apply();
			  });
			
			$scope.$on('activeAgentForRemoval', function (event, arg) { 	
				$rootScope.activeAgents = $rootScope.activeAgents.filter(function(el){
					var ret = true;
					arg.forEach(function(element) {
						  if(el.name == element.name && el.host.name == element.host.name)
							  ret = false;
						  	  return;
					});
					return ret;
				});
				$scope.$apply();
			  });
			
			this.stopAgent = (aid) =>{ 
				agentService.stopAgent(aid)
				.then( (response) => {
					var a = aid; 
					$rootScope.activeAgents = $rootScope.activeAgents.filter(function(el){
						return el !== a;
					});
				}, () => {
					alert('Could not stop agent')
				});
				
			}
		}
	});		