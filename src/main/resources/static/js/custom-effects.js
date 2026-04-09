/* ============================================
   EngCenter Special Effects System
   Fireworks & Money Rain Implementation
   ============================================ */

const EngCenterEffects = {
    // ---------- FIREWORKS ----------
    triggerFireworks: function() {
        const canvas = document.createElement('canvas');
        canvas.id = 'ec-fireworks-canvas';
        canvas.style.position = 'fixed';
        canvas.style.top = '0';
        canvas.style.left = '0';
        canvas.style.width = '100%';
        canvas.style.height = '100%';
        canvas.style.pointerEvents = 'none';
        canvas.style.zIndex = '9999';
        document.body.appendChild(canvas);

        const ctx = canvas.getContext('2d');
        let width = canvas.width = window.innerWidth;
        let height = canvas.height = window.innerHeight;

        const particles = [];
        const colors = ['#4F46E5', '#06B6D4', '#F59E0B', '#10B981', '#EF4444', '#FFFFFF'];

        class Particle {
            constructor(x, y, color) {
                this.x = x;
                this.y = y;
                this.color = color;
                this.velocity = {
                    x: (Math.random() - 0.5) * 8,
                    y: (Math.random() - 0.5) * 8
                };
                this.alpha = 1;
                this.friction = 0.95;
            }

            draw() {
                ctx.globalAlpha = this.alpha;
                ctx.beginPath();
                ctx.arc(this.x, this.y, 2, 0, Math.PI * 2);
                ctx.fillStyle = this.color;
                ctx.fill();
            }

            update() {
                this.velocity.x *= this.friction;
                this.velocity.y *= this.friction;
                this.x += this.velocity.x;
                this.y += this.velocity.y;
                this.alpha -= 0.01;
            }
        }

        function createFirework(x, y) {
            const color = colors[Math.floor(Math.random() * colors.length)];
            for (let i = 0; i < 40; i++) {
                particles.push(new Particle(x, y, color));
            }
        }

        let animationFrame;
        function animate() {
            ctx.clearRect(0, 0, width, height);
            particles.forEach((p, i) => {
                if (p.alpha <= 0) {
                    particles.splice(i, 1);
                } else {
                    p.update();
                    p.draw();
                }
            });
            animationFrame = requestAnimationFrame(animate);
        }

        animate();

        // Launch some initial fireworks
        let count = 0;
        const interval = setInterval(() => {
            createFirework(Math.random() * width, Math.random() * height * 0.7);
            count++;
            if (count > 15) {
                clearInterval(interval);
                setTimeout(() => {
                    cancelAnimationFrame(animationFrame);
                    canvas.remove();
                }, 3000);
            }
        }, 300);
    },

    // ---------- MONEY RAIN ----------
    triggerMoneyRain: function() {
        const container = document.createElement('div');
        container.style.position = 'fixed';
        container.style.top = '0';
        container.style.left = '0';
        container.style.width = '100%';
        container.style.height = '100%';
        container.style.pointerEvents = 'none';
        container.style.zIndex = '9998';
        container.style.overflow = 'hidden';
        document.body.appendChild(container);

        const moneyChars = ['💵', '💰', '💸', '✨', '💎'];
        
        for (let i = 0; i < 50; i++) {
            setTimeout(() => {
                const money = document.createElement('div');
                money.innerText = moneyChars[Math.floor(Math.random() * moneyChars.length)];
                money.style.position = 'absolute';
                money.style.top = '-50px';
                money.style.left = Math.random() * 100 + '%';
                money.style.fontSize = (Math.random() * 20 + 20) + 'px';
                money.style.transition = 'top ' + (Math.random() * 2 + 2) + 's linear, left 2s ease-in-out';
                container.appendChild(money);

                // Start falling
                setTimeout(() => {
                    money.style.top = '110%';
                    money.style.transform = 'rotate(' + (Math.random() * 360) + 'deg)';
                }, 100);

                // Cleanup
                setTimeout(() => money.remove(), 5000);
            }, i * 100);
        }

        setTimeout(() => container.remove(), 10000);
    },

    // ---------- INITIALIZER ----------
    init: function() {
        const urlParams = new URLSearchParams(window.location.search);
        
        // Success Registered -> Fireworks
        if (urlParams.get('success') === 'registered' || document.getElementById('ec-trigger-fireworks')) {
            this.triggerFireworks();
        }

        // Success Paid -> Money Rain
        if (urlParams.get('success') === 'paid' || document.getElementById('ec-trigger-money')) {
            this.triggerMoneyRain();
            // Show a nice toast if toastr is available or just alert
            console.log("Payment successful! Triggering money rain...");
        }
    }
};

// Auto-init on load
$(document).ready(function() {
    EngCenterEffects.init();
});
