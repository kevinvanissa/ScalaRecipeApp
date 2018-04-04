import React from 'react';
import ReactDOM from 'react-dom';
import './vendor/jquery-3.2.1.slim.min.js';
import './vendor/popper.min.js';
import './vendor/bootstrap.min.js';
import RecipeComponent from './recipe-component.jsx';
//import RecipeListComponent from './recipe-component.jsx';

ReactDOM.render(<RecipeComponent />, document.getElementById('reactView'));
//ReactDOM.render(<RecipeListComponent />, document.getElementById('reactViewList'));
