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
			 
//			$rootScope.agentClasses = [];
			
//			$scope.$on('agentClasses', function (event, arg) { 
//				
//				$rootScope.agentClasses = arg.filter(function(el){
//					return !el.agentClass.includes("Local");
//				});
//				$scope.$apply();
//			  });
			
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