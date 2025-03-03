(function(){
    const canvas = document.getElementById("nbodyCanvas");
    const ctx = canvas.getContext("2d");

    let particles = [];
    const ws = new WebSocket("ws://localhost:8025/ws/nbody");

ws.onopen = () => {
    console.log("WS 2D connecté !");
    ws.send(JSON.stringify({ command: "startSimulation" })); // Force le démarrage
};


    ws.onmessage = (event) => {
        const data = JSON.parse(event.data);

        // Filtrer la masse centrale
        particles = data.particles.filter(p => p.index !== 0);

        render(particles);
    };

    ws.onclose = () => {
        console.log("WS 2D fermé");
    };

    ws.onerror = (err) => {
        console.error("WS 2D erreur:", err);
    };

    document.getElementById("restartBtn").addEventListener("click", () => {
        const numParticles = parseInt(document.getElementById("numParticles").value);
        const msg = { command: "resetSimulation", numParticles: numParticles };
        ws.send(JSON.stringify(msg));
    });

// Sélection des éléments
const dtInput = document.getElementById("dtInput");
const dtValue = document.getElementById("dtValue");

// Mise à jour de l'affichage initial
dtValue.innerText = parseFloat(dtInput.value).toExponential(5);

// Écouteur d'événement pour mettre à jour la valeur affichée en temps réel
dtInput.addEventListener("input", function () {
    dtValue.innerText = parseFloat(this.value).toExponential(5);
});

// Appliquer la mise à jour seulement quand le curseur est relâché
dtInput.addEventListener("change", function() {
    const dtValueFloat = parseFloat(this.value);

    // Envoi au serveur WebSocket après relâchement
    const msg = {
        command: "updateDt",
        dt: dtValueFloat
    };
    ws.send(JSON.stringify(msg));
    console.log("DT mis à jour après relâchement du curseur :", dtValueFloat);
});


    document.getElementById("startBtn").addEventListener("click", function() {
        const msg = { command: "startSimulation" };
        ws.send(JSON.stringify(msg));
    });

    document.getElementById("stopBtn").addEventListener("click", function() {
        const msg = { command: "stopSimulation" };
        ws.send(JSON.stringify(msg));
    });

    document.getElementById("restartBtn").addEventListener("click", function() {
        const numParticles = parseInt(document.getElementById("numParticles").value);
        const msg = {
            command: "resetSimulation",
            numParticles: numParticles
        };
        ws.send(JSON.stringify(msg));
    });
    function render(particles) {
        const cx = canvas.width / 2;
        const cy = canvas.height / 2;
        const scale = 30;

        // Effet de traînée persistante pour donner une impression de continuité
        ctx.fillStyle = "rgba(0, 0, 0, 0.6)";
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        // Dessiner les traînées rouges pour simuler un effet de gravité
        ctx.fillStyle = "rgba(255, 0, 0, 0.2)";
        particles.forEach(p => {
            const px = cx + p.position.x * scale;
            const py = cy + p.position.y * scale;
            ctx.beginPath();
            ctx.arc(px, py, 1, 0, 2 * Math.PI);
            ctx.fill();
        });

        // Effet de halo et poussière d'étoile
        particles.forEach(p => {
            const px = cx + p.position.x * scale;
            const py = cy + p.position.y * scale;

            // Effet de halo autour de chaque particule
            ctx.beginPath();
            ctx.arc(px, py, 4, 0, 2 * Math.PI); // Halo plus grand
            ctx.fillStyle = "rgba(255, 255, 255, 0.05)";
            ctx.fill();

            // Dessiner la particule principale en blanc
            ctx.fillStyle = "white";
            ctx.fillRect(px - 1, py - 1, 2, 2);
        });

        // Effet de poussière d'étoile
        ctx.fillStyle = "rgba(255, 255, 255, 0.1)";
        for (let i = 0; i < 50; i++) { // Générer 50 particules de poussière aléatoires
            const px = cx + (Math.random() - 0.5) * canvas.width; // Position aléatoire
            const py = cy + (Math.random() - 0.5) * canvas.height;
            ctx.fillRect(px, py, 1, 1); // Petits points pour simuler la poussière d'étoiles
        }
    }
})();
