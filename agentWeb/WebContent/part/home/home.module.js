'use strict';

angular.module('home', [
	'ui.router',
	'home.sidebar',
	'userAuth',
	'core.wsService',
	'chat',
	'agent_list',
	'groups'
]);
