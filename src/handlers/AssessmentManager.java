package handlers;

import database.DatabaseConnectionManager;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import models.Mark;
import models.ProjectMark;

/**
 *
 * @author Roger NDJEUMOU
 */
public class AssessmentManager {
    
    public static final String ASSIGNMENT = "Assignment Marks" ;
    public static final String QUIZ = "Quiz Marks" ;
    public static final String TEST = "Test Marks" ;
    public static final String PROJECT = "Project Marks" ;
    
    private ArrayList<ProjectMark> projectMarks = new ArrayList<>() ;
    private ArrayList<Mark> assignmentMarks = new ArrayList<>() ;
    private ArrayList<Mark> quizMarks = new ArrayList<>() ;
    private ArrayList<Mark> testMarks = new ArrayList<>() ;
    // All average marks take are on 20
    private double averageAssignmentMark ;
    private double averageQuizMark ;
    private double averageTestMark ;
    private double averageProjectMark ;

    public AssessmentManager(String idStudent, String idCourse) {
        DatabaseConnectionManager manager = new DatabaseConnectionManager() ;
        
        this.assignmentMarks = manager.getAllAssignmentMarks(idCourse, idStudent) ;
        this.quizMarks = manager.getAllQuizMarks(idCourse, idStudent) ;
        this.testMarks = manager.getAllTestMarks(idCourse, idStudent) ;
        this.projectMarks = manager.getAllProjectMarks(idCourse, idStudent) ;
        // Bring the assignment mark on 20 by multiplying by 2.
        this.averageAssignmentMark = 2 * manager.getAverageAssignmentMark(idCourse, idStudent) ;
        this.averageQuizMark = manager.getAverageQuizMark(idCourse, idStudent) ;
        this.averageTestMark = manager.getAverageTestMark(idCourse, idStudent) ;
        this.averageProjectMark = manager.getAverageProjectMark(idCourse, idStudent) ;
    }
    
    public TitledPane getOverallMarksPane(){
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList() ;
        double fail = 0 ;
        if(averageAssignmentMark != 0 ) {
            fail += (20 - averageAssignmentMark) ;
            pieChartData.add(
                    new PieChart.Data(
                            String.format("Assignment (%.2f)", averageAssignmentMark),
                            averageAssignmentMark)) ;
        }
        if(averageQuizMark != 0 ) {
            fail += (20 - averageQuizMark) ;
            pieChartData.add(new PieChart.Data(
                    String.format("Quiz (%.2f)", averageQuizMark),
                    averageQuizMark)) ;
        }
        if(averageTestMark != 0 ) {
            fail += (20 - averageTestMark) ;
            pieChartData.add(new PieChart.Data(
                    String.format("Test (%.2f)", averageTestMark),
                    averageTestMark)) ;
        }
        if(averageProjectMark != 0 ) {
            fail += (20 - averageProjectMark) ;
            pieChartData.add(
                    new PieChart.Data(
                            String.format("Project (%.2f)", averageProjectMark), 
                            averageProjectMark)) ;
        }
        
        pieChartData.add(new PieChart.Data(
                String.format("Fail rate (%.2f", fail)+"%)",
                fail)) ;
        
        final PieChart chart = new PieChart(pieChartData) ;
        chart.setLegendSide(Side.LEFT);
        chart.setLabelLineLength(10);
        // Add mouse pressed event
        Label caption = new Label("") ;
        caption.setTextFill(Color.rgb(209, 172, 109));
        for(PieChart.Data data : pieChartData){
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    (MouseEvent event) -> {
                        caption.setTranslateX(event.getSceneX());
                        caption.setTranslateY(event.getSceneY());
                        caption.setText(String.format("%.2f", data.getPieValue()) +"%");
                    }); 
        }
        
        return new TitledPane("Average marks", chart) ;
    }
    
    public TitledPane getMarksPane(String assessmentType) {
        final CategoryAxis xAxis = new CategoryAxis() ;
        final NumberAxis yAxis = new NumberAxis() ;
        final BarChart<String, Number> barChart = 
                new BarChart<>(xAxis,yAxis) ;
        xAxis.setLabel("Date");
        yAxis.setLabel("Mark");
        switch (assessmentType) {
            case QUIZ -> barChart.getData().add( getChartData(quizMarks) );
            case TEST -> barChart.getData().add( getChartData(testMarks) );
            case PROJECT -> barChart.getData().add( getProjectChartData(projectMarks) );
            default -> barChart.getData().add( getChartData(assignmentMarks) );
        }
                
        return new TitledPane(assessmentType, barChart) ;
    }
        
    public boolean isAnyMarkAvailable(){
        return (averageAssignmentMark != 0 || averageQuizMark != 0
                || averageTestMark != 0 || averageProjectMark != 0) ;
    }
    
    public boolean areMarksAvailable(String assessmentType){
        return switch (assessmentType) {
            case QUIZ -> ! quizMarks.isEmpty();
            case TEST -> ! testMarks.isEmpty();
            case PROJECT -> ! projectMarks.isEmpty();
            default -> ! assignmentMarks.isEmpty();
        };        
    }
    
    private XYChart.Series getChartData(ArrayList<Mark> marks) {
        XYChart.Series series = new XYChart.Series() ;
        for (Mark mark : marks) {
            series.getData().add(
                    new XYChart.Data(
                            mark.getDate().toString(), 
                            mark.getMark()
                    )
            );
        }
        
        return series ;
    }
    
    private XYChart.Series getProjectChartData(ArrayList<ProjectMark> marks) {
        XYChart.Series series = new XYChart.Series() ;
        for (ProjectMark mark : marks) {
            series.getData().add(
                    new XYChart.Data(
                            mark.getDate().toString(), 
                            mark.getMark()
                    )
            );
        }
        
        return series ;
    }
}
