#!/usr/bin/python
import os
print """
<!DOCTYPE html>
<html>
<head>
    <script src="../js/jquery-1.12.0.min.js"></script>
    <link href="../css/metro.css" rel="stylesheet">
    <script src="../js/metro.min.js"></script>
	<link href="../css/metro-icons.css" rel="stylesheet">

<body>
    <div class="page-content">

	<div class="container">
  
"""
if os.environ['REMOTE_ADDR'].count("172.12.12") and os.path.exists("wifistate/.__GLOBAL__"):
	print "<H3>Note: aaaBREWERSLAB local wifi access point currently active - this will switch to %s in less than 10 minutes</H3>" %( open("wifistate/.__GLOBAL__").read().split(":")[0])
	print "To remove the wireless settings <a href='removewifi.py'>click here</a>"
	
print """
		    <div class="tile-container">

                <a href="javascript:showDialog('#poweroffDialog')" class="tile bg-crimson fg-white" data-role="tile">
                    <div class="tile-content iconic">
                        <span class="icon mif-settings-power"></span>
                    </div>
                    <span class="tile-label">Power Off</span>
                </a>




                <a href="settings.py" class="tile bg-crimson fg-white" data-role="tile">
                    <div class="tile-content iconic">
                        <span class="icon mif-cogs"></span>
                    </div>
                    <span class="tile-label">Setings</span>
                </a>

		</div>


		    <div class="tile-container">

                <a href="temps.py" class="tile bg-teal fg-white" data-role="tile">
                    <div class="tile-content iconic">
                        <span class="icon mif-meter"></span>
                    </div>
                    <span class="tile-label">Readings</span>
                </a>
	"""

if 1==1:
	print """
			<a href="javascript:startMonitoring()" class="tile bg-green fg-white" data-role="tile" id="startTile">
			    <div class="tile-content iconic" >
				<span class="icon mif-play"></span>
			    </div>
			    <span class="tile-label">Start Monitoring</span>
			</a>

		"""
print """
		<div class="tile" id="activeTile" style="display: none">
                    <div class="tile-content iconic" >
                        <span class="icon mif-play"></span>
                    </div>
                    <span class="tile-label">Monitoring Active</span>

		</div>

		</div>

	

	    <div class="tile-container">

                <a href="camera.py" class="tile bg-darkBlue fg-white" data-role="tile">
                    <div class="tile-content iconic">
                        <span class="icon mif-camera"></span>
                    </div>
                    <span class="tile-label">Camera</span>
                </a>

		</div>

	
<p>Last Reading: <span id=lastReading>...</span>
	</div>
    </div>

"""
if os.path.exists("/tmp/standalone-temp-active"):
	print """
		<script language="Javascript">
			$("#startTile").hide();
			$("#activeTile").show();
		</script>
	"""

print """

<script language=Javascript>

function updateLastReading(){
		$.ajax({
		    url: "lastreading.py",
		    error: function(){
			setTimeout(updateLastReading,3000);
		    },
		    success: function(xml){
			if( $(xml).find('status')){
				if($(xml).find('status').text() == "0"){

					$("#lastReading").html("");
					readings=parseInt($(xml).find('readings').text());
					for(r=0;r<readings;r++){
						reading=$(xml).find('reading'+r).text();
						$("#lastReading").append( reading);
					}
				}else{
					$("#lastReading").html( $(xml).find('msg').text() );
				}
			}
			setTimeout(updateLastReading,3000);
		    },
		    timeout: 5000 
		});
}


setTimeout(updateLastReading,3000);

function startMonitoring(){

		$("#startTile").hide();
		$("#activeTile").show();


		$.ajax({
		    url: "start.py?start=True",
		    error: function(){
			alert("Unable to start monitoring");
		    },
		    success: function(xml){
			alert("Monitoring Started");
		    },
		    timeout: 5000 // sets timeout to 3 seconds
		});

		

}

</script>

<div data-role="dialog" id="poweroffDialog" style='padding: 15px'>
    <h1>Power Off</h1>
    <p>

        <b>Are you sure you want to shutdown?</b></P>

	Password: <!-- Input with reveal helper -->
<div class="input-control password" data-role="input">
    <input type="password" id='password'>
    <button class="button helper-button reveal"><span class="mif-looks"></span></button>
</div>

    <p><span style='display: none' id='shutdownWarning' class='fgRed'>Note: please wait 5 minutes after clicking <I>SHUTDOWN</I> before removing the power.</span>    </p>

    <p align="right">
	<input id='poweroffDialogCancelButton' type='button' value='Cancel' onClick="cancelDialog('poweroffDialog')"> &nbsp; - &nbsp;
	<input type='button' value='SHUTDOWN' onClick="doShutdown()" id='poweroffDialogShutdownButton'>
    </p>
</div>
<script>
    function doShutdown(){
	if($("#password").val() == ""){
		alert("Password not provided - cannot shutdown");
	}else{
		$("#shutdownWarning").show();
		document.getElementById("poweroffDialogShutdownButton").disabled=true;
		document.getElementById("poweroffDialogCancelButton").disabled=true;

		$.ajax({
		    url: "poweroff.py?poweroff=True&adminpass="+$("#password").val(),
		    success: function(xml){
			alert("Poweroff started");
		    },
		    timeout: 3000 // sets timeout to 3 seconds
		});

	}
    }
    function cancelDialog(id){	
	$("#"+id).hide();
    }
    function showDialog(id){
        var dialog = $(id).data('dialog');
        dialog.open();
	$(id).show();
    }
</script>





<footer class='app-bar fixed-bottom drop-up'>brewerslab - <a href="https://github.com/allena29/brewerslab/"><span class='fg-white'> &nbsp; <span class="mif-github"></span> &nbsp;github.com/allena29/brewerslab</span></a></footer>

</body>



</html>
"""
