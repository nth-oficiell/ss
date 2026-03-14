// Produits
const products = [
    { id: 'nitro-promo-3m', name: 'Nitro Promo 3 mois', price: 3, emoji: '🚀', category: 'nitro', stock: 999, isNew: true },
    { id: 'nitro-1m', name: 'Nitro 1 mois', price: 4, emoji: '⚡', category: 'nitro', stock: 999 },
    { id: 'boost-x14-1m', name: 'Boost x14 1 mois', price: 3.5, emoji: '💎', category: 'boost', stock: 999 },
    { id: 'boost-x14-3m', name: 'Boost x14 3 mois', price: 8.5, emoji: '💎', category: 'boost', stock: 999 },
    { id: 'bot-perso', name: 'Bot Personnalisé', price: 10.5, emoji: '🤖', category: 'bot', stock: 50, onTicket: true },
    { id: 'bot-raid', name: 'Bot Raid', price: 7, emoji: '🛡️', category: 'bot', stock: 50 },
    { id: 'membres-online', name: 'Online x1000', price: 5.5, emoji: '👥', category: 'membres', stock: 999 },
    { id: 'membres-offline', name: 'Offline x1000', price: 4.7, emoji: '👤', category: 'membres', stock: 999 },
    { id: 'premium-lifetime', name: 'Premium Lifetime', price: 5.3, emoji: '👑', category: 'compte', stock: 30, isNew: true },
    { id: 'backup-serveur', name: 'Backup Serveur', price: 4, emoji: '💾', category: 'serveur', stock: 999 },
    { id: 'panel-bot', name: 'Panel Bot +300', price: 5, emoji: '🎛️', category: 'panel', stock: 300 }
];

let orders = [];
let config = {
    paypalMe: 'https://www.paypal.me/Karimsix',
    discordInvite: 'https://discord.gg/kzlook'
};

// Token simple
const encode = data => Buffer.from(JSON.stringify(data)).toString('base64');
const decode = token => { try { return JSON.parse(Buffer.from(token, 'base64').toString()); } catch { return null; } };

export default async function handler(req, res) {
    const { method, url } = req;
    const path = url.split('?')[0];

    // CORS rapide
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
    if (method === 'OPTIONS') return res.status(200).end();

    // Auth Discord
    if (path === '/api/auth/discord') {
        const redirect = `${process.env.VERCEL_URL}/api/auth/callback`;
        return res.redirect(`https://discord.com/api/oauth2/authorize?client_id=${process.env.DISCORD_CLIENT_ID}&redirect_uri=${redirect}&response_type=code&scope=identify`);
    }

    if (path === '/api/auth/callback') {
        try {
            const { code } = req.query;
            const tokenRes = await fetch('https://discord.com/api/oauth2/token', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({
                    client_id: process.env.DISCORD_CLIENT_ID,
                    client_secret: process.env.DISCORD_CLIENT_SECRET,
                    code,
                    grant_type: 'authorization_code',
                    redirect_uri: `${process.env.VERCEL_URL}/api/auth/callback`
                })
            });
            const { access_token } = await tokenRes.json();
            const userRes = await fetch('https://discord.com/api/users/@me', { headers: { Authorization: `Bearer ${access_token}` } });
            const user = await userRes.json();
            const token = encode({ id: user.id, username: user.username, avatar: `https://cdn.discordapp.com/avatars/${user.id}/${user.avatar}.png` });
            return res.redirect(`/?token=${token}`);
        } catch (e) {
            return res.status(500).json({ error: 'Auth failed' });
        }
    }

    if (path === '/api/auth/verify') {
        const user = decode(req.query.token);
        return user ? res.json(user) : res.status(401).json({ error: 'Invalid token' });
    }

    // Produits
    if (path === '/api/products') {
        const { category } = req.query;
        let filtered = products.filter(p => p.active !== false);
        if (category && category !== 'all') filtered = filtered.filter(p => p.category === category);
        return res.json(filtered);
    }

    // Commandes
    if (path === '/api/orders' && method === 'POST') {
        const { productId, paypalName, discordId, discordName } = req.body;
        const product = products.find(p => p.id === productId);
        if (!product || product.stock <= 0) return res.status(400).json({ error: 'Stock insuffisant' });
        
        const order = {
            id: `ORD_${Date.now()}`,
            discordId, discordName, productId, productName: product.name,
            amount: product.price, paypalName, status: 'pending',
            createdAt: new Date().toISOString()
        };
        orders.push(order);
        product.stock--;
        
        return res.json({ success: true, amount: product.price, paypalLink: config.paypalMe });
    }

    // Config
    if (path === '/api/config') return res.json(config);

    // Admin
    if (path === '/api/admin/auth' && method === 'POST') {
        return req.body.password === 'kzlook2026ontop' 
            ? res.json({ success: true, adminToken: encode({ isAdmin: true }) })
            : res.status(401).json({ success: false });
    }

    // Vérification admin pour les routes protégées
    const adminData = req.headers.authorization ? decode(req.headers.authorization) : null;
    if (!adminData?.isAdmin) return res.status(401).json({ error: 'Unauthorized' });

    if (path === '/api/admin/stats') {
        return res.json({
            totalSales: orders.reduce((s, o) => s + o.amount, 0),
            pendingOrders: orders.filter(o => o.status === 'pending').length,
            totalStock: products.reduce((s, p) => s + p.stock, 0)
        });
    }

    if (path === '/api/admin/orders' && method === 'GET') return res.json(orders);

    if (path.startsWith('/api/admin/products/') && method === 'PUT') {
        const p = products.find(x => x.id === path.split('/').pop());
        if (p) { p.price = req.body.price ?? p.price; p.stock = req.body.stock ?? p.stock; }
        return res.json({ success: true });
    }

    if (path === '/api/admin/config' && method === 'PUT') {
        if (req.body.paypalMe) config.paypalMe = req.body.paypalMe;
        if (req.body.discordInvite) config.discordInvite = req.body.discordInvite;
        return res.json({ success: true });
    }

    return res.status(404).json({ error: 'Not found' });
}
