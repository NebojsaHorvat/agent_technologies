'use strict';

angular.module('agent_list')
	.component('myAgentList', {
		templateUrl: 'part/agent_list/agent_list.template.html',
		controller: function( $rootScope, $state,wsService,$scope,$element,agentService) {
			wsService.getAgentClasses();
			 var agentListVm = this;
			 
			$rootScope.agentClasses = [];
			
			$scope.$on('agentClasses', function (event, arg) { 
				
				$rootScope.agentClasses = arg.filter(function(el){
					return !el.agentClass.includes("Local");
				});
				$scope.$apply();
			  });
			
			$scope.$on('agentClassesForRemoval', function (event, arg) { 
				
				$rootScope.agentClasses = $rootScope.agentClasses.filter(function(el){
					var ret = true;
					arg.forEach(function(element) {
						  if(el.agentClass == element.agentClass)
							  ret = false;
						  	  return;
					});
					return ret;
				});
				$scope.$apply();
			  });

			this.send = (agentClass,agentName) =>{
				agentService.activateAgent(agentClass.agentClass,agentName,agentClass.host)
				.then( (response) => {
					
				}, () => {
					alert('Could not activate agent')
				});
				
			}
		}
	});		