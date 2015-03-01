(function () {
  function formatDate(d){
    return d.toISOString().slice(0, 19)
  }
  
   var templ = document.getElementById("result-template");
  var ractive = new Ractive({
    el: 'container',
    template: '#template',
    partials: { result: templ.innerHTML },
    data: { asDate: 
              function(d){
                return formatDate(new Date(d));
              }
          }         
  });
  
  ractive.on( 'search', function ( event, query ) {
    event.original.preventDefault()
    $.getJSON('service/search/' + query)
       .done(function(data) { 
         ractive.set('results', data); 
       });   
  });
      
  function updateStatus(status){
    ractive.set("count", status.eventCount);
    ractive.set("healthy", status.healthy);
    ractive.set("lastUpdated", formatDate(new Date()));
  }    
      
  function doPoll(){
    $.getJSON('service/status')
      .done(function(status) { updateStatus(status) })
      .always(function() { setTimeout(doPoll, 10000); });   
  }    
  doPoll();    
}());    
