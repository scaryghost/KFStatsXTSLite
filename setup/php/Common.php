<?php

/**
 * Common functions used by the pages
 * @author etsai
 */
class Common {
    /**
     * Converts an input of seconds into "D days HH:MM:SS" format
     * @param   $time   Number of seconds to convert
     * @return  String representation of time in "D days HH:MM:SS" format
     */
    public static function formatTime($time) {
        $seconds= $time % 60;
        $minutes= ($time / 60) % 60;
        $hours= ($time / 3600) % 24;
        $days= (($time / 3600) / 24);

        return sprintf("%d days %02d:%02d:%02d", $days, $hours, $minutes, $seconds);
    }

    /**
     * Create a pie chart from the Google Charts API.  The code is based on the sample code from this page: 
     * https://google-developers.appspot.com/chart/interactive/docs/gallery/piechart
     * @param   $dataUrl    URL to query for data
     * @param   $width      Width of the pie chart
     * @param   $height     Height of the pie chart
     * @param   $divId      ID of the div containing the pie chart
     * @param   $title      Title of the pie chart
     */
    public static function generatePieChart($dataUrl, $width, $height, $divId, $title) {
?>
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript" src="jquery-1.8.2.js"></script>
        <script type="text/javascript">
    
            google.load('visualization', '1', {'packages':['corechart']});
            google.setOnLoadCallback(drawChart);
      
            function drawChart() {
                var jsonData = $.ajax({
                    url: "<?php echo $dataUrl; ?>",
                    dataType:"json",
                    async: false
                }).responseText;
          
                var data = new google.visualization.DataTable(jsonData);
                var chart = new google.visualization.PieChart(document.getElementById('<?php echo $divId; ?>'));
                chart.draw(data, {width: <?php echo $width; ?>, height: <?php echo $height; ?>, title: '<?php echo $title; ?>' });
            }
        </script>
<?php
    }

    /**
     * Create a table from the Google Charts API.  The code is based on the sample code from this page:
     * https://google-developers.appspot.com/chart/interactive/docs/gallery/table
     * @param   $dataUrl    URL to query for data
     * @param   $width      Width of the table
     * @param   $height     Height of the table
     * @param   $divId      ID of the div containing the table
     */
    public static function generateTable($dataUrl, $width, $height, $divId) {
?>
        <script type='text/javascript' src='https://www.google.com/jsapi'></script>
        <script type="text/javascript" src="jquery-1.8.2.js"></script>
        <script type='text/javascript'>

            google.load('visualization', '1', {packages:['table']});
            google.setOnLoadCallback(drawTable);

            function drawTable() {
                var jsonData = $.ajax({
                    url: "<?php echo $dataUrl; ?>",
                    dataType:"json",
                    async: false
                }).responseText;
          
                var data= new google.visualization.DataTable(jsonData);
                var table= new google.visualization.Table(document.getElementById('<?php echo $divId; ?>'));
                table.draw(data, {width: <?php echo $width; ?>, height: <?php echo $height; ?>, allowHtml: true});
            }
        </script>
<?php
    }
}

?>
