# Simulation à N-Corps avec WebSocket

## 📌 Présentation du projet

Ce projet est une **simulation à N-Corps** utilisant **WebSockets** pour la communication entre le **backend en Java** et le **frontend en JavaScript**.  
Il implémente **l'algorithme Barnes-Hut avec QuadTree** pour optimiser les calculs gravitationnels et réduire la complexité de **O(N²) à O(N log N)**.

## 📁 Structure du projet

```bash
myproject/
├── BACKEND/
│   ├── pom.xml
│   ├── src/
│   │   ├── main/java/com/example/
│   │   │   ├── App.java
│   │   │   ├── controller/NBodyEndpoint.java
│   │   │   ├── model/
│   │   │   │   ├── Particle.java
│   │   │   │   ├── QuadTree.java
│   │   │   │   ├── Vector2.java
│   │   │   ├── service/NBodyService.java
│   │   ├── test/java/com/example/
│   │   │   ├── controller/NBodyEndpointTest.java
│   │   │   ├── model/
│   │   │   │   ├── ParticleTest.java
│   │   │   │   ├── QuadTreeTest.java
│   │   │   │   ├── Vector2Test.java
│   │   │   ├── service/NBodyServiceTest.java
│   ├── target/
├── FRONTEND/
│   ├── IMAGE/
│   │   ├── QuadTree_image.png
│   │   ├── application_interface.png
│   │   └── logo_youtube.png
│   ├── index.html
│   └── main.js
└── README.md

## 🌍 Explication de l'algorithme

La simulation repose sur **l'algorithme de Barnes-Hut**, qui est une optimisation du problème des N-corps en gravitation. 

### 🧮 Méthode QuadTree

L'algorithme repose sur une division récursive de l'espace en quadrants grâce à une structure de données QuadTree :

-Division de l'espace : L'espace est divisé en quatre quadrants de manière récursive jusqu'à ce que chaque quadrant contienne une seule particule ou un nombre minimal de particules.
-Centre de masse : Pour chaque quadrant contenant plusieurs particules, on calcule le centre de masse et la masse totale du groupe.
-Calcul des forces : Si un groupe de particules est suffisamment éloigné, on traite ce groupe comme une seule particule (en utilisant son centre de masse). Sinon, on subdivise le quadrant pour calculer les forces individuellement.

### 🧮Méthode Naïve en  O (N2) 

  Dans la méthode naïve, chaque particule interagit directement avec toutes les autres particules. Cela signifie que pour  N  particules, on effectue  N×(N−1) calculs de force gravitationnelle.  
  Complexité :  O (N2), ce qui devient rapidement impraticable pour un grand nombre de particules (par exemple, 10 000 particules nécessitent 100 millions de calculs). 
  Avantage : Simplicité de mise en œuvre. 
  Inconvénient : Non scalable pour des simulations de grande envergure. 
  ✅ Avantages de Barnes-Hut  
  ✅ Réduction de la complexité : de O(N²) à O(N log N) grâce à la hiérarchie du QuadTree. 
  ✅ Gestion efficace des grandes simulations : permet de calculer des milliers de particules en temps réel. 
  ✅ Utilisation optimisée des WebSockets : mise à jour en continu via un serveur WebSocket. 
   
📷 **Représentation de la méthode QuadTree**
<img src="frontend/images/QuadTree_image.png" alt="QuadTree" width="500">


### ✅ Avantages de Barnes-Hut
✅ **Réduction de la complexité** : de **O(N²)** à **O(N log N)** grâce à la hiérarchie du QuadTree.  
✅ **Gestion efficace des grandes simulations** : permet de calculer des milliers de particules en temps réel.  
✅ **Utilisation optimisée des WebSockets** : mise à jour en continu via un serveur WebSocket.

## 🚀 Lancer la simulation

### 1️⃣ Lancer le serveur WebSocket (Backend Java)

bash
cd backend
mvn clean package
java -jar target/nbody-simulator-1.0-SNAPSHOT.jar


### 2️⃣ Lancer l'interface graphique (Frontend JavaScript)

Ouvrir frontend/index.html dans un navigateur ou exécuter un serveur local :

bash
cd frontend
python3 -m http.server 8080


Puis accéder à :
http://localhost:8080


📷 **Interface de la simulation**

![Application Interface](frontend/images/application_interface.png)

## 🧪 Lancer les tests

bash
cd backend
mvn test


## 📡 Interaction avec le WebSocket

| Commande JSON | Effet |
|--------------|-------------------------|
| { "command": "startSimulation" } | Démarrer la simulation |
| { "command": "stopSimulation" } | Arrêter la simulation |
| { "command": "resetSimulation", "numParticles": 500 } | Réinitialiser avec 500 particules |
| { "command": "updateDt", "dt": 0.0001 } | Modifier le pas de temps |

## 🔗 Ressources utiles

- [Documentation WebSocket](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)
- [Algo Barnes-Hut](https://en.wikipedia.org/wiki/Barnes%E2%80%93Hut_simulation)

## 📺 Démo Vidéo

🎥 **Regarde la démo sur YouTube** :  
[![YouTube](frontend/images/logo_youtube.png)](https://youtube.com/)

---

🚀 **Projet réalisé par [Barbedet Anthony](https://github.com/tonybarbedet)**
