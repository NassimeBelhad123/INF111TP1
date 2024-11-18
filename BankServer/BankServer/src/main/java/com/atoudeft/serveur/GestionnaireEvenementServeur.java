package com.atoudeft.serveur;

import com.atoudeft.banque.*;
import com.atoudeft.banque.serveur.ConnexionBanque;
import com.atoudeft.banque.serveur.ServeurBanque;
import com.atoudeft.commun.evenement.Evenement;
import com.atoudeft.commun.evenement.GestionnaireEvenement;
import com.atoudeft.commun.net.Connexion;

import java.util.List;

import static com.atoudeft.banque.TypeCompte.CHEQUE;
import static com.atoudeft.banque.TypeCompte.EPARGNE;

/**
 * Cette classe représente un gestionnaire d'événement d'un serveur. Lorsqu'un serveur reçoit un texte d'un client,
 * il crée un événement à partir du texte reçu et alerte ce gestionnaire qui réagit en gérant l'événement.
 *
 * @author Abdelmoumène Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-01
 */
public class GestionnaireEvenementServeur implements GestionnaireEvenement {
    private Serveur serveur;

    /**
     * Construit un gestionnaire d'événements pour un serveur.
     *
     * @param serveur Serveur Le serveur pour lequel ce gestionnaire gère des événements
     */
    public GestionnaireEvenementServeur(Serveur serveur) {
        this.serveur = serveur;
    }

    /**
     * Méthode de gestion d'événements. Cette méthode contiendra le code qui gère les réponses obtenues d'un client.
     *
     * @param evenement L'événement à gérer.
     */
    double solde =0;

    @Override
    public void traiter(Evenement evenement) {
        Object source = evenement.getSource();
        ServeurBanque serveurBanque = (ServeurBanque)serveur;
        Banque banque;
        ConnexionBanque cnx;
        String msg, typeEvenement, argument, numCompteClient, nip;
        String[] t;



        if (source instanceof Connexion) {
            cnx = (ConnexionBanque) source;
            String numeroCompte1 = cnx.getNumeroCompteClient();
            CompteCheque compteClient1 = new CompteCheque(numeroCompte1, CHEQUE);
            System.out.println("SERVEUR: Recu : " + evenement.getType() + " " + evenement.getArgument());
            typeEvenement = evenement.getType();
            cnx.setTempsDerniereOperation(System.currentTimeMillis());
            switch (typeEvenement) {
                /******************* COMMANDES GÉNÉRALES *******************/
                case "EXIT": //Ferme la connexion avec le client qui a envoyé "EXIT":
                    cnx.envoyer("END");
                    serveurBanque.enlever(cnx);
                    cnx.close();
                    break;
                case "LIST": //Envoie la liste des numéros de comptes-clients connectés :
                    cnx.envoyer("LIST " + serveurBanque.list());
                    break;







                /******************* Connexion *******************/
                case "CONNECT":
                    argument = evenement.getArgument();
                    t = argument.split(":");
                    if(t.length< 2){
                        cnx.envoyer("CONNECT NO");
                        break;
                    }
                    numCompteClient = t[0];
                    nip = t[1];

                    String listeConnectes = serveurBanque.list();
                    if (listeConnectes.contains(numCompteClient + ":")) {
                        cnx.envoyer("CONNECT NO deja connecte");
                        break;
                    }

                    banque = serveurBanque.getBanque();
                    CompteClient compteClient = banque.getCompteClient(numCompteClient);
                    if (compteClient == null) {
                        cnx.envoyer("CONNECT NO existe pas");
                        break;
                    }
                    String nipStocke = compteClient.getNip();
                    if (nip.equals(nipStocke)) {
                        cnx.setNumeroCompteClient(numCompteClient);
                        cnx.setNumeroCompteActuel(banque.getNumeroCompteParDefaut(numCompteClient));
                        cnx.envoyer("CONNECT OK");
                    } else {
                        cnx.envoyer("CONNECT NOO");
                    }
                    break;







                case "EPARGNE":
                    argument = evenement.getArgument();
                    t = argument.split(":");

                    // Vérification si le client est connecter
                    if (cnx.getNumeroCompteClient()==null) {
                        cnx.envoyer("EPARGNE NO client non connecté");
                        break;
                    }
                    else{
                        cnx.envoyer("EPARGNE OK" + cnx.getNumeroCompteClient());
                    }
                    banque = serveurBanque.getBanque();
                    numCompteClient = cnx.getNumeroCompteClient();
                    CompteClient client = banque.getCompteClient(numCompteClient);

                    String nouveauNumeroCompte;
                    do {
                        nouveauNumeroCompte = CompteBancaire.genereNouveauNumero();
                    } while (banque.getCompteClient(nouveauNumeroCompte) != null);


                    CompteEpargne compteEpargne = new CompteEpargne(nouveauNumeroCompte, EPARGNE, 5.0);
                    boolean compteAjoute = client.ajouter(compteEpargne);
                    if(compteAjoute){
                        cnx.envoyer("EPARGNE OK " + nouveauNumeroCompte + compteEpargne.getType());

                    }else{
                        cnx.envoyer("ERREUR: Le compte épargne n'a pas pu être ajouté.");
                    }


                    break;













                case "SELECT":
                    if (cnx.getNumeroCompteClient() == null) {
                        cnx.envoyer("SELECT NO pas de connexion");
                        break;
                    }

                    argument = evenement.getArgument();


                    String numeroCompte = null;
                    if ("cheque".equalsIgnoreCase(argument)) {
                        numeroCompte = (cnx.getNumeroCompteClient());
                    } else if ("epargne".equalsIgnoreCase(argument)) {
                    } else {
                        cnx.envoyer("SELECT NO argument invalide");
                        break;
                    }

                    if (numeroCompte != null) {
                        cnx.setNumeroCompteActuel(numeroCompte);
                        cnx.envoyer("SELECT OK " + argument);
                    } else {
                        cnx.envoyer("SELECT NO compte introuvable");
                    }
                    break;









                case "DEPOT":
                    if (cnx.getNumeroCompteClient() == null ) {
                        cnx.envoyer("DEPOT NO pas de compte sélectionné" + cnx.getNumeroCompteClient());
                        break;
                    }

                    argument = evenement.getArgument();
                    double montant;
                    try {
                        montant = Double.parseDouble(argument);
                        if (montant <= 0) {
                            cnx.envoyer("DEPOT NO montant invalide");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        cnx.envoyer("DEPOT NO format incorrect");
                        break;
                    }


                    if (compteClient1.crediter(montant)) {
                        solde +=montant;
                        compteClient1.setSolde(solde);
                        cnx.envoyer("DEPOT OK " + montant + " déposé dans le compte " + numeroCompte1 + " " + compteClient1.getSolde());
                    } else {
                        cnx.envoyer("DEPOT NO erreur lors du dépôt");
                        break;
                    }
                    break;







                case "FACTURE":
                    if (cnx.getNumeroCompteClient() == null) {
                        cnx.envoyer("FACTURE NO pas de compte sélectionné");
                        break;
                    }

                    argument = evenement.getArgument();
                    t = argument.split(" ", 3);

                    if (t.length < 3) {
                        cnx.envoyer("FACTURE NO format incorrect");
                        break;
                    }

                    double montantFacture;
                    try {
                        montantFacture = Double.parseDouble(t[0]);
                        if (montantFacture <= 0) {
                            cnx.envoyer("FACTURE NO montant invalide");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        cnx.envoyer("FACTURE NO format de montant incorrect");
                        break;
                    }

                    String numeroFacture = t[1];
                    String description = t[2];

                    if (compteClient1.debiter(montantFacture)) {
                        compteClient1.setSolde(solde- montantFacture);
                        cnx.envoyer("FACTURE OK " + montantFacture + " payé pour " + numeroFacture + " " + description + ". Nouveau solde: " + compteClient1.getSolde());
                    } else {
                        cnx.envoyer("FACTURE NO solde insuffisant pour payer " + montantFacture);
                    }

                    break;






                case "RETRAIT":
                    if(cnx.getNumeroCompteClient()==null){
                        cnx.envoyer("Pas de compte sélectionné");
                        break;
                    }
                    cnx.envoyer("" + compteClient1.getSolde());
                    argument = evenement.getArgument();
                    try {
                        montant = Double.parseDouble(argument);
                        if(solde==0){
                            cnx.envoyer("Solde du compte insuffisant");
                            break;
                        }
                        if (montant <= 0) {
                            cnx.envoyer("Retrait NO montant invalide");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        cnx.envoyer("Retrait NO format incorrect");
                        break;
                    }
                    if (compteClient1.debiter(montant)) {
                        solde -= montant;
                        cnx.envoyer("Retrait OK " + montant + " déposé dans le compte " + solde);
                    } else {
                        cnx.envoyer("Retrait NO erreur lors du dépôt");
                        break;


                    }
                    break;

























                /******************* COMMANDES DE GESTION DE COMPTES *******************/
                case "NOUVEAU": //Crée un nouveau compte-client :
                    if (cnx.getNumeroCompteClient()!=null) {
                        cnx.envoyer("NOUVEAU NO deja connecte");
                        break;
                    }
                    argument = evenement.getArgument();
                    t = argument.split(":");
                    if (t.length<2) {
                        cnx.envoyer("NOUVEAU NO");
                    }
                    else {
                        numCompteClient = t[0];
                        nip = t[1];
                        banque = serveurBanque.getBanque();
                        if (banque.ajouter(numCompteClient,nip)) {
                            cnx.setNumeroCompteClient(numCompteClient);
                            cnx.setNumeroCompteActuel(banque.getNumeroCompteParDefaut(numCompteClient));
                            cnx.envoyer("NOUVEAU OK " + t[0] + " cree " );

                        }
                        else
                            cnx.envoyer("NOUVEAU NO "+t[0]+" existe");
                    }
                    break;



                case "HIST":
                    if (cnx.getNumeroCompteClient() == null) {
                        cnx.envoyer("HIST NO pas de compte sélectionné");
                        break;
                    }


                    PileChainee historique = compteClient1.getHistorique();
                    if (historique == null || historique.estVide()) {
                        cnx.envoyer("HIST OK Historique vide");
                    } else {
                        while (!historique.estVide()) {
                            String operation = (String) historique.depiler();
                            cnx.envoyer("HIST " + operation);
                        }

                    }
                    break;





                /******************* TRAITEMENT PAR DÉFAUT *******************/
                default: //Renvoyer le texte recu convertit en majuscules :
                    msg = (evenement.getType() + " " + evenement.getArgument()).toUpperCase();
                    cnx.envoyer(msg);
            }
        }
    }
}