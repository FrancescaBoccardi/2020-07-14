/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnClassifica"
    private Button btnClassifica; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimula"
    private Button btnSimula; // Value injected by FXMLLoader

    @FXML // fx:id="cmbSquadra"
    private ComboBox<Team> cmbSquadra; // Value injected by FXMLLoader

    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doClassifica(ActionEvent event) {
    	
    	Team squadra = this.cmbSquadra.getValue();
    	
    	if(squadra==null) {
    		this.txtResult.setText("Devi prima selezionare una squadra");
    		return;
    	}
    	
    	// da sistemare dopo 
    	model.classificaSquadra(squadra);
    	
    	this.txtResult.setText("SQUADRE MIGLIORI:\n");
    	
    	for(Team t : model.getMigliori().keySet()) {
    		this.txtResult.appendText(t+"("+model.getMigliori().get(t)+")\n");
    	}
    	
    	this.txtResult.appendText("\nSQUADRE PEGGIORI:\n");
    	
    	for(Team t : model.getPeggiori().keySet()) {
    		this.txtResult.appendText(t+"("+model.getPeggiori().get(t)+")\n");
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	this.model.creaGrafo();
    	int vertici = this.model.getGrafo().vertexSet().size();
    	int archi = this.model.getGrafo().edgeSet().size();
    	
    	txtResult.setText("Grafo creato:\n#Vertici: "+vertici+"\n#Archi: "+archi);
    	
    	this.cmbSquadra.getItems().addAll(this.model.getGrafo().vertexSet()); //popolo tendina squadre
    }

    @FXML
    void doSimula(ActionEvent event) {
    	
    	int N = Integer.parseInt(this.txtN.getText());
    	int X = Integer.parseInt(this.txtX.getText());
    	
    	this.model.simula(N, X);
    	
    	this.txtResult.setText("Media reporter a partita: "+this.model.getMediaReporterPartita()+"\nNumero partite con reporter minori di "+X+": "+model.getNumPartiteMinX());
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnClassifica != null : "fx:id=\"btnClassifica\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbSquadra != null : "fx:id=\"cmbSquadra\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}
