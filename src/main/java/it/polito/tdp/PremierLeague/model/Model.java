package it.polito.tdp.PremierLeague.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Team,DefaultWeightedEdge> grafo;
	private Map<Integer,Team> idMap;
	Map<Integer,Integer> classifica;
	Map<Team,Double> migliori;
	Map<Team,Double> peggiori;
	
	
	// Modello del mondo
		
	private List<Match> matches;
	private Map<Integer, Integer> reporterSquadra;
		
	// Parametri di input
		
	private int N; 
	private int X; 

	// Parametri di output
	
	private int totReporter;
	private double mediaReporterPartita;
	private int numPartiteMinX;
	
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.idMap = new HashMap<>();
		this.dao.listAllTeams(idMap);
		this.matches = this.dao.listAllMatches();
	}
	
	public void creaGrafo() {
		
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiungo i vertici
		
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		// aggiungo gli archi
		
		classifica = new HashMap<>(); //squadra - punteggio
		
		for(Team t : idMap.values()) {
			classifica.put(t.teamID, 0);
		}
		
		for(Match m : matches) {
			if(m.getResultOfTeamHome()==0) {
				classifica.put(m.getTeamHomeID(),classifica.get(m.getTeamHomeID())+1);
				classifica.put(m.getTeamAwayID(),classifica.get(m.getTeamAwayID())+1);
			}
			
			if(m.getResultOfTeamHome()==-1) {
				classifica.put(m.getTeamAwayID(),classifica.get(m.getTeamAwayID())+3);
			}
			
			if(m.getResultOfTeamHome()==1) {
				classifica.put(m.getTeamHomeID(),classifica.get(m.getTeamHomeID())+3);
			}
		}
		
		for(Integer id1 : classifica.keySet()) {
			for(Integer id2 : classifica.keySet()) {
				
				if(id1!=id2) {
					
					int peso = classifica.get(id1)-classifica.get(id2);
					
					if(peso>0 && !this.grafo.containsEdge(idMap.get(id1), idMap.get(id2))) {
						Graphs.addEdge(this.grafo, idMap.get(id1), idMap.get(id2), peso);
					} else if(peso<0 && !this.grafo.containsEdge(idMap.get(id1), idMap.get(id2))) {
						Graphs.addEdge(this.grafo, idMap.get(id2), idMap.get(id1), peso*(-1));
					}
				}
				
			}
		}

	}
	
	public void classificaSquadra(Team squadra) {
		
		migliori = new HashMap<>();
		peggiori = new HashMap<>();
		
		for(DefaultWeightedEdge e : grafo.incomingEdgesOf(squadra) ) {
			migliori.put(grafo.getEdgeSource(e), grafo.getEdgeWeight(e));
		}
		
		
		for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(squadra) ) {
			peggiori.put(grafo.getEdgeTarget(e), grafo.getEdgeWeight(e));
		}
		
	}
	
	public void simula(int N, int X){
		this.init(N,X);
		this.run();
		
		this.mediaReporterPartita = this.totReporter/matches.size();
	}
	
	
	// Inizializza il simulatore e crea gli eventi iniziali
	private void init(int N, int X) {
		
		// inizializza modello del mondo
		this.N=N;
		this.X=X;
		this.reporterSquadra = new HashMap<>();
		
		
		// inizializza i parametri di output
		this.mediaReporterPartita=0;
		this.numPartiteMinX=0;
		totReporter=0;
		
		// inietta gli eventi di input
		
		for(Team t : grafo.vertexSet()) {
			this.reporterSquadra.put(t.getTeamID(), N);
		}
	}
	
	private void run() {
		for(Match m : matches) {
			
			totReporter += (this.reporterSquadra.get(m.getTeamHomeID()) + this.reporterSquadra.get(m.getTeamAwayID()));
			
			if((this.reporterSquadra.get(m.getTeamHomeID()) + this.reporterSquadra.get(m.getTeamAwayID()))<X) {
				this.numPartiteMinX++;
			}
			
			if(m.getResultOfTeamHome()==1) { //squadra casa vincente
				if(Math.random()<=0.5) { // squadra vincente
					if(this.reporterSquadra.get(m.getTeamHomeID())>0) { // se ci sono reporter associati alla squadra
						
						List<Team> best = new ArrayList<>(migliori.keySet());
						
						if(best.size()>0) {
							this.reporterSquadra.put(m.getTeamHomeID(), reporterSquadra.get(m.getTeamHomeID())-1); // tolgo un reporter alla squadra
							Team nuova = best.get((int)Math.random()*(best.size()-1));
							this.reporterSquadra.put(nuova.getTeamID(), reporterSquadra.get(nuova.getTeamID())+1); // ne aggiungo uno ad una squadra migliore scelta casualmente
						}
					}
				}
				
				if(Math.random()<=0.2) { // squadra perdente
					if(this.reporterSquadra.get(m.getTeamAwayID())>0) { // se ci sono reporter associati alla squadra
						
						List<Team> worst = new ArrayList<>(peggiori.keySet());
						
						if(worst.size()>0) {
							int reporterBocciati = (int)Math.random()*(this.reporterSquadra.get(m.getTeamAwayID())-1)+1; // numero di reporter da bocciare
							this.reporterSquadra.put(m.getTeamAwayID(), reporterSquadra.get(m.getTeamAwayID())-reporterBocciati); // tolgo tot reporter alla squadra
							Team nuova = worst.get((int)Math.random()*(worst.size()-1));
							this.reporterSquadra.put(nuova.getTeamID(), reporterSquadra.get(nuova.getTeamID())+reporterBocciati); // li aggiungo ad una squadra peggiore scelta casualmente
						}
					}
				}
			}
			
			if(m.getResultOfTeamHome()==-1) {
				if(Math.random()<=0.5) { // squadra vincente
					if(this.reporterSquadra.get(m.getTeamAwayID())>0) { // se ci sono reporter associati alla squadra
						
						List<Team> best = new ArrayList<>(migliori.keySet());
						
						if(best.size()>0) {
							this.reporterSquadra.put(m.getTeamAwayID(), reporterSquadra.get(m.getTeamAwayID())-1); // tolgo un reporter alla squadra
							Team nuova = best.get((int)Math.random()*(best.size()-1));
							this.reporterSquadra.put(nuova.getTeamID(), reporterSquadra.get(nuova.getTeamID())+1); // ne aggiungo uno ad una squadra migliore scelta casualmente
						}
					}
				}
				
				if(Math.random()<=0.2) { // squadra perdente
					if(this.reporterSquadra.get(m.getTeamHomeID())>0) { // se ci sono reporter associati alla squadra
						
						List<Team> worst = new ArrayList<>(peggiori.keySet());
						
						if(worst.size()>0) {
							int reporterBocciati = (int)Math.random()*(this.reporterSquadra.get(m.getTeamHomeID())-1)+1; // numero di reporter da bocciare
							this.reporterSquadra.put(m.getTeamHomeID(), reporterSquadra.get(m.getTeamHomeID())-reporterBocciati); // tolgo tot reporter alla squadra
							Team nuova = worst.get((int)Math.random()*(worst.size()-1));
							this.reporterSquadra.put(nuova.getTeamID(), reporterSquadra.get(nuova.getTeamID())+reporterBocciati); // li aggiungo ad una squadra peggiore scelta casualmente
						}
					}
				}
			}
		}
	}

	public Graph<Team,DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public double getMediaReporterPartita() {
		return mediaReporterPartita;
	}

	public int getNumPartiteMinX() {
		return numPartiteMinX;
	}

	public Map<Team, Double> getMigliori() {
		return migliori;
	}

	public Map<Team, Double> getPeggiori() {
		return peggiori;
	}
	
	
	
	
}
