// Genera docs/Iparhotel_Memoria.pdf a partir de memoria.html con Chromium (Playwright).
// Uso: node docs/fuente/generar-pdf.js
const path = require('path');
const { chromium } = require('playwright');

(async () => {
  const html = path.join(__dirname, 'memoria.html');
  const pdf = path.join(__dirname, '..', 'Iparhotel_Memoria.pdf');
  const navegador = await chromium.launch();
  const pagina = await navegador.newPage();
  await pagina.goto('file://' + html, { waitUntil: 'load' });
  await pagina.pdf({
    path: pdf,
    format: 'A4',
    printBackground: true,
    preferCSSPageSize: true,
    displayHeaderFooter: true,
    headerTemplate: '<span></span>',
    footerTemplate:
      '<div style="font-size:8px;color:#888;width:100%;text-align:center;font-family:Arial">Iparhotel · Memoria del proyecto · <span class="pageNumber"></span> / <span class="totalPages"></span></div>',
  });
  await navegador.close();
  console.log('PDF generado:', pdf);
})();
