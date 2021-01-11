importScripts('/VAADIN/static/server/workbox/workbox-sw.js');

workbox.setConfig({
    modulePathPrefix: '/VAADIN/static/server/workbox/'
});
workbox.precaching.precacheAndRoute([
    { url: 'icons/icon-64x64.png', revision: '-1456135562' },
    { url: 'icons/icon-144x144.png', revision: '-1456135562' },
    { url: 'icons/icon-192x192.png', revision: '-1333786034' },
    { url: 'icons/icon-250x250.png', revision: '-1456135562' },
    { url: 'icons/icon-512x512.png', revision: '1931390955' },
    { url: 'offline.html', revision: '1470818919' },
    { url: 'manifest.json', revision: '-166760670' }
]);
self.addEventListener('fetch', function(event) {
    var request = event.request;
    if (request.mode === 'navigate') {
        event.respondWith(
            fetch(request)
            .catch(function() {
                return caches.match('offline.html');
            })
        );
    }
});