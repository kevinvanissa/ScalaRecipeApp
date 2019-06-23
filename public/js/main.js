

function calculatev(nutval, descval,amount){
		var score = ((nutval/100) * descval) * amount;
		return score.toFixed(2);
}

function deleteItem(){
    if(confirm("This action will delete the selected item. Are you sure you want to continue?")){
        return true;
}
    return false;
}



$(document).ready(function(){

$("#pounds_input").keyup(function(){
    var pounds = $("#pounds_input").val();
    var ans = pounds * 0.45359237

   $("#pounds").html('=> <b>'+ ans.toFixed(2) + ' kg</b>');
});


$("#feet_input").keyup(function(){
    var feet = $("#feet_input").val();
    var ans = feet * 30.48

   $("#feet").html('=> <b>'+ ans.toFixed(2) + ' cm</b>');
});




 $("#description").change(function(){
 	var amount = $("#amount").val();
  		var description = $("#description").val();
  		var description = description.split(" ")[0]
  		var protein = $("#protein").val();
  		var fat = $("#fat").val();
  		var carbohydrate = $("#carbohydrate").val();
  		var calories = $("#calories").val();

         var pscore = calculatev(protein, description, amount);
         var fscore = calculatev(fat, description, amount);
         var cscore = calculatev(carbohydrate, description, amount);
         var calscore = calculatev(calories, description, amount);

         var data = "<span class='fSize'>Protein: "+pscore+" (g) | "+"Fats: "+fscore+" (g) | " + "Carbohydrate: "+cscore+" (g) | " +"Calories: "+calscore+" (g) </span><br />"
         
 		$("#foodvals").html(data);

    });

  $("#amount").keyup(function(){
  		var amount = $("#amount").val();
  		var description = $("#description").val();
  		//alert(description);
  		var description = description.split(" ")[0]
  		//alert(description);
  		var protein = $("#protein").val();
  		var fat = $("#fat").val();
  		var carbohydrate = $("#carbohydrate").val();
  		var calories = $("#calories").val();

         var pscore = calculatev(protein, description, amount);
         var fscore = calculatev(fat, description, amount);
         var cscore = calculatev(carbohydrate, description, amount);
         var calscore = calculatev(calories, description, amount);

         var data = "<span class='fSize'>Protein: "+pscore+" (g) | "+"Fats: "+fscore+" (g) | " + "Carbohydrate: "+cscore+" (g) | " +"Calories: "+calscore+" (g) </span> <br />"
         
 		$("#foodvals").html(data);
    });


  $( function() {
    $(".mytooltip2").tooltip({
    open: function(event, ui){
      var id = this.id
      var split_id = id.split('_')
      var foodid = split_id[1]
      //alert(foodid);
     //$("#food_"+foodid).tooltip("option","content","Hello World_"+foodid);

     $.ajax({
     url:'/getlongdes',
     type:'post',
     data:{foodid:foodid},
     success: function(response){
         $("#food_"+foodid).tooltip("option","content",response);
     }


     })//ajax

     }//end open

    });//end tooltip
  } );




  $( function() {
    $(".mytooltip3").tooltip({
    open: function(event, ui){
      var id = this.id
      var split_id = id.split('_')
      var foodid = split_id[1]
      //alert(foodid);
     //$("#food_"+foodid).tooltip("option","content","Hello World_"+foodid);

     $.ajax({
     url:'/getlongdes',
     type:'post',
     data:{foodid:foodid},
     success: function(response){
         $("#food3_"+foodid).tooltip("option","content",response);
     }


     })//ajax

     }//end open

    });//end tooltip
  } );



$(".mytooltip2").mouseout(function(){
   // re-initializing tooltip
   $(this).attr('title','Please wait...');
   $(this).tooltip();
   $('.ui-tooltip').hide();
 });


$(".mytooltip3").mouseout(function(){
   // re-initializing tooltip
   $(this).attr('title','Please wait...');
   $(this).tooltip();
   $('.ui-tooltip').hide();
 });





});


