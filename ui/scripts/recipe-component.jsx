import React from 'react';
import axios from 'axios';

class RecipeComponent extends React.Component{
    constructor(props){
      super(props);
      this.state={
        recipename:null,
        rdescription:null,
        recipeimage: null
      };
    }
    componentDidMount = () => {
  axios.get('/recipe').then((response) => {
    const json = response.data;
    this.setState({
      recipename: json.name,
      rdescription: json.description,
      recipeimage: json.rimage,
    });
  })

  axios.get('/recipes').then((response) => {
    const json = response.data;
    this.setState({
      recipes: "did"
    });
  })

};
    render = () => {
      return <div>
      <div>Recipe Name: {this.state.recipename}</div>
      <div>Description: {this.state.rdescription}</div>
      <div>Image: {this.state.recipeimage}</div>
      <div><Content /></div>
      <div><RecipeListComponent /></div>

      </div>
    }
}




class Content extends React.Component {
   render() {
      return (
         <div>
            <h2>Content</h2>
            <p>The content text!!!</p>
         </div>
      );
   }
}


class RecipeListComponent extends React.Component {
   render() {
      return (
         <div>
            <h2>Content2</h2>
            <p>{1+1}</p>
         </div>
      );
   }
}





export default RecipeComponent;
