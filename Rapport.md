## Saunier Léo, Dantec Malo, Kerguen Tony, Devers--Doré Lucas

# SAE 3.03 Programmation système et architecture des réseaux

### Manuel d'utilisation : 

Pour commencer il faut lancer le serveur grâce au fichier  Server.java  
Enuite grâce à la classe Client et à la commande ``java Client <IP serveur> <Nom du serveur>`` cela nous connecte au serveur.

#### Les commandes : 

- connect
- ask
- list
- disconnect 
- accept
- reject
- history
- mv

La commande ``connect`` permet de t'attribuer un nom en faisant ``connect <Nom d'utilisateur>``.  

La commande ``ask`` permet d'envoyer une demande pour affronter un autre joueur, voici un exemple ``ask <Nom du joueur>``.

La commande ``list`` permet de voir toutes les personnes connectés au serveur. Cela retourne une liste dans le terminal. 

La commande ``disconnect`` te déconnecte du serveur.

La commande ``accept`` te permet d'accepter la requete de la commande ``ask``.

La commande ``reject`` te permet de refuser la requete de la commande ``ask``.

La commande ``history`` te permet de voir l'historique de tes parties. Elle est conservée tant que le serveur ne s'éteint pas.  

Pour finir la commande ``mv`` te permet de placer une piece pendant une partie en faisant ``mv <Nom de la colonne>``. 

