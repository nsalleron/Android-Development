Architecture du projet :
Langage : Kotlin
Étudiant : Nicolas Salleron - 3504018
Packages : 
	- activities :	Regroupe les activités de l'application.
	- adapter : 	Regroupe les adapteurs de l'application.
	- data : 		Regroupe les données de l'application.
	- fragments : 	Regroupe les fragments de l'application.

Classes: 

Package activities :
	- GameFragmentActivity : extends de FragmentActivity, permet la gestion du viewPager des fragments StreetFragment et MapFragment.
	- MainActivity : extends de Activity, c'est l'écran de démarrage, permet d'avoir des boutons redirigeant vers le GameFragmentActivity ou le ScoreAboutFragmentActivity.
	- ScoreAboutFragmentActivity: extends de FragmentActivity, permet la gestion du viewPager des fragments ScoreFragment et AboutFragment.

Package adapter : 
	- GameAdapter : Permets de retourner le titre, le fragment et le nombre de fragment. (Utilisé par GrameFragmentActivity, setAdapter du viewPager)
	- ScoreAboutAdapter :  Permets de retourner le titre, le fragment et le nombre de fragments. (Utilisé par ScoreAboutFragmentActivity, setAdapter du viewPager)
	- ListAdapter : Permets de définir les éléments qui iront dans l'affichage des statistiques locales de l'utilisateur (écran ScoreAboutFragmentActivity).

Package data : 
	- City : permets de regrouper différentes informations sur la ville (lat, lng, name, country, help, snippet). 
		-	Le champ "help" est pour l'affichage d'une astuce au début de la recherche de chaque ville. 
		-	Le champ "snippet" permet l'affichage d'une information supplémentaire sur la ville quand on clique sûr le marker sur le fragment "map".
	- Difficulty : Regroupe les différentes villes en niveaux de difficulté.

Package fragments:
	- AboutFragment : Contiens une webView chargeant une page locale. 
		-	Se contente d'afficher les différentes librairies utilisées dans l'application ainsi que la licence d'utilisation de chaque application. 
		-	Ne gère pas la restriction de domaine.
	- MapFragment : Initialise la map de l'application (GoogleMap), retire certaines interactions présentes par défaut.
	- ScoreFragment : 
		- Récupère le nom de l'utilisateur (Google Games) si aucun nom d'utilisateur local n'est défini. 
		- Permet de définir un nom d'utilisateur local. 
		- Permet de voir les exploits (succès) de l'utilisateur ainsi que le leaderboard. 
		- Affiche les statistiques de chaque partie de l'utilisateur
	- StreetFragment : Initialise la streetView de l'application, retire certaines interactions/aide présentes par défaut. 


Les options implémentées : 
	- Gestion de la rotation sur l'ensemble des écrans.
	- Utilisation du ViewPager pour l'écran "jeu" et l'écran "statistiques / about".
	- Utilisation de l'API Games de Google pour :
		- affichage du plus haut score de l'utilisateur,
		- affichage des succès débloqués ou non,
		- affichage de la leaderboard,
		- comparaison de scores avec d'autres joueurs,
		- partage de score.
	- Persistance durable en local.
	- Partage de score à la fin des 15 "niveaux".
	- Possibilité de définir un nom d'utilisateur en local (avec possibilité de le modifier).
	- Mode de jeu dans lequel si l'utilisateur clique sur le bon pays c'est gagné!
	- Mode de jeu avec chronomètre dans lequel il faut répondre aux 15 questions avant le temps imparti. 
		PAS DE DÉPLACEMENT DE L'UTILISATEUR REQUIS => Ne corresponds pas vraiment à l'option présentée sur les slides.