"""Genera los diagramas UML en SVG para la memoria (casos de uso y clases de la fase 1)."""
import sys
from pathlib import Path

SALIDA = Path(sys.argv[1]) if len(sys.argv) > 1 else Path(__file__).parent
FUENTE = 'font-family="Arial, Helvetica, sans-serif"'
LINEA = 18


def esc(t):
    return t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")


# ---------------------------------------------------------------- clases
def clase(x, y, w, nombre, atributos, metodos, estereotipo=None, abstracta=False):
    """Caja UML con tres compartimentos. Devuelve (svg, alto)."""
    cab = 40 if estereotipo else 28
    alto_atr = len(atributos) * LINEA + 10
    alto_met = len(metodos) * LINEA + 10
    h = cab + alto_atr + alto_met
    s = [f'<rect x="{x}" y="{y}" width="{w}" height="{h}" fill="#fff" stroke="#2f2f2f" stroke-width="1.3"/>']
    s.append(f'<rect x="{x}" y="{y}" width="{w}" height="{cab}" fill="#e7f1ec" stroke="#2f2f2f" stroke-width="1.3"/>')
    ty = y + 18
    if estereotipo:
        s.append(f'<text x="{x + w / 2}" y="{ty - 2}" text-anchor="middle" font-size="11" {FUENTE}>{esc(estereotipo)}</text>')
        ty += 15
    estilo = ' font-style="italic"' if abstracta else ''
    s.append(f'<text x="{x + w / 2}" y="{ty}" text-anchor="middle" font-size="13" font-weight="bold"{estilo} {FUENTE}>{esc(nombre)}</text>')
    yy = y + cab
    s.append(f'<line x1="{x}" y1="{yy + alto_atr}" x2="{x + w}" y2="{yy + alto_atr}" stroke="#2f2f2f"/>')
    for i, a in enumerate(atributos):
        s.append(f'<text x="{x + 8}" y="{yy + 18 + i * LINEA}" font-size="12" {FUENTE}>{esc(a)}</text>')
    yy += alto_atr
    for i, m in enumerate(metodos):
        italica = ' font-style="italic"' if m.endswith("{abstract}") else ''
        s.append(f'<text x="{x + 8}" y="{yy + 18 + i * LINEA}" font-size="12"{italica} {FUENTE}>{esc(m)}</text>')
    return "\n".join(s), h


def triangulo(x, y, direccion):
    """Punta de herencia/realizacion (triangulo hueco) con el vertice en (x, y)."""
    if direccion == "arriba":
        pts = f"{x},{y} {x - 9},{y + 16} {x + 9},{y + 16}"
    else:  # derecha
        pts = f"{x},{y} {x - 16},{y - 9} {x - 16},{y + 9}"
    return f'<polygon points="{pts}" fill="#fff" stroke="#2f2f2f" stroke-width="1.3"/>'


def diagrama_clases():
    p = []
    hab, h_hab = clase(20, 20, 300, "Habitacion",
                       ["- id: Long", "- numero: String", "- capacidad: int", "- precioNoche: BigDecimal", "- disponible: boolean"],
                       ["+ calcularPrecio(noches: int): BigDecimal {abstract}", "+ getTipo(): String {abstract}",
                        "+ equals(o: Object): boolean", "+ hashCode(): int", "+ toString(): String"],
                       abstracta=True)
    p.append(hab)
    res, h_res = clase(400, 20, 290, "Reserva",
                       ["- id: Long", "- habitacion: Habitacion", "- fechaEntrada: LocalDate", "- noches: int", "- importe: BigDecimal"],
                       ["+ Reserva(id, habitacion, fechaEntrada, noches)", "+ getFechaSalida(): LocalDate",
                        "+ getImporte(): BigDecimal", "+ setNoches(noches: int)"])
    p.append(res)
    # Asociacion Reserva -> Habitacion (muchas reservas de una habitacion)
    p.append('<line x1="320" y1="80" x2="400" y2="80" stroke="#2f2f2f" stroke-width="1.3"/>')
    p.append(f'<text x="328" y="72" font-size="12" {FUENTE}>1</text>')
    p.append(f'<text x="388" y="72" font-size="12" {FUENTE}>*</text>')

    # Excepcion
    rt, _ = clase(750, 20, 190, "RuntimeException", [], [])
    p.append(rt)
    ex, h_ex = clase(735, 130, 220, "ReservaException", [], ["+ ReservaException(mensaje: String)"])
    p.append(ex)
    p.append('<line x1="845" y1="130" x2="845" y2="64" stroke="#2f2f2f" stroke-width="1.3"/>')
    p.append(triangulo(845, 48, "arriba"))
    # Dependencia Reserva --> ReservaException
    p.append('<line x1="690" y1="200" x2="731" y2="200" stroke="#2f2f2f" stroke-width="1.3" stroke-dasharray="6 4"/>')
    p.append('<polyline points="721,194 733,200 721,206" fill="none" stroke="#2f2f2f" stroke-width="1.3"/>')
    p.append(f'<text x="712" y="190" text-anchor="middle" font-size="11" {FUENTE}>«throws»</text>')

    # Subclases
    fila = 430
    ind, h1 = clase(20, fila, 245, "Individual", [], ["+ calcularPrecio(noches: int): BigDecimal", "+ getTipo(): String"])
    dob, h2 = clase(285, fila, 245, "Doble", ["- camaSupletoria: boolean"],
                    ["+ calcularPrecio(noches: int): BigDecimal", "+ getTipo(): String"])
    sui, h3 = clase(550, fila, 250, "Suite", ["- jacuzzi: boolean"],
                    ["+ calcularPrecio(noches: int): BigDecimal", "+ getTipo(): String", "+ getPorcentajeDescuento(): int"])
    p += [ind, dob, sui]
    pro, h4 = clase(690, 280, 260, "Promocionable", [],
                    ["+ getPorcentajeDescuento(): int", "+ aplicarDescuento(importe): BigDecimal"],
                    estereotipo="«interface»")
    p.append(pro)

    # Herencia: las tres subclases -> Habitacion
    base = 20 + h_hab
    p.append(triangulo(170, base, "arriba"))
    p.append(f'<line x1="170" y1="{base + 16}" x2="170" y2="400" stroke="#2f2f2f" stroke-width="1.3"/>')
    p.append('<line x1="142" y1="400" x2="675" y2="400" stroke="#2f2f2f" stroke-width="1.3"/>')
    for cx in (142, 407, 675):
        p.append(f'<line x1="{cx}" y1="400" x2="{cx}" y2="{fila}" stroke="#2f2f2f" stroke-width="1.3"/>')
    # Realizacion: Suite ..|> Promocionable
    p.append(f'<line x1="800" y1="500" x2="880" y2="500" stroke="#2f2f2f" stroke-width="1.3" stroke-dasharray="6 4"/>')
    p.append(f'<line x1="880" y1="500" x2="880" y2="{280 + h4 + 16}" stroke="#2f2f2f" stroke-width="1.3" stroke-dasharray="6 4"/>')
    p.append(triangulo(880, 280 + h4, "arriba"))

    alto = fila + max(h1, h2, h3) + 20
    return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 960 {alto}" width="960" height="{alto}">\n' + "\n".join(p) + "\n</svg>"


# ---------------------------------------------------------------- casos de uso
def actor(x, y, nombre):
    return (f'<circle cx="{x}" cy="{y}" r="11" fill="#fff" stroke="#2f2f2f" stroke-width="1.5"/>'
            f'<line x1="{x}" y1="{y + 11}" x2="{x}" y2="{y + 42}" stroke="#2f2f2f" stroke-width="1.5"/>'
            f'<line x1="{x - 18}" y1="{y + 22}" x2="{x + 18}" y2="{y + 22}" stroke="#2f2f2f" stroke-width="1.5"/>'
            f'<line x1="{x}" y1="{y + 42}" x2="{x - 15}" y2="{y + 64}" stroke="#2f2f2f" stroke-width="1.5"/>'
            f'<line x1="{x}" y1="{y + 42}" x2="{x + 15}" y2="{y + 64}" stroke="#2f2f2f" stroke-width="1.5"/>'
            f'<text x="{x}" y="{y + 84}" text-anchor="middle" font-size="13" font-weight="bold" {FUENTE}>{nombre}</text>')


def caso(x, y, texto, rx=118):
    return (f'<ellipse cx="{x}" cy="{y}" rx="{rx}" ry="25" fill="#e7f1ec" stroke="#2f6b4f" stroke-width="1.4"/>'
            f'<text x="{x}" y="{y + 4}" text-anchor="middle" font-size="13" {FUENTE}>{texto}</text>')


def diagrama_casos_uso():
    p = ['<rect x="240" y="15" width="680" height="535" rx="6" fill="none" stroke="#2f2f2f" stroke-width="1.3"/>',
         f'<text x="580" y="38" text-anchor="middle" font-size="14" font-weight="bold" {FUENTE}>Sistema Iparhotel</text>']
    actores = {"Anónimo": (90, 70), "Cliente": (90, 255), "Administrador": (90, 430)}
    for nombre, (x, y) in actores.items():
        p.append(actor(x, y, nombre))
    casos = {
        "ver": (450, 85, "Ver habitaciones disponibles"),
        "login": (450, 160, "Iniciar sesión"),
        "reservar": (450, 250, "Reservar habitación"),
        "mis": (450, 325, "Ver mis reservas"),
        "salir": (450, 395, "Cerrar sesión"),
        "admin": (450, 495, "Ver todas las reservas"),
        "importe": (775, 250, "Calcular importe"),
    }
    radio = {k: (128 if len(t) > 22 else 112) for k, (_, _, t) in casos.items()}
    for k, (x, y, t) in casos.items():
        p.append(caso(x, y, t, rx=radio[k]))
    # Asociaciones actor - caso de uso
    for actor_y, destinos in ((105, ["ver", "login"]), (290, ["reservar", "mis", "salir"]), (465, ["admin"])):
        for d in destinos:
            x, y, _ = casos[d]
            p.append(f'<line x1="112" y1="{actor_y}" x2="{x - radio[d]}" y2="{y}" stroke="#2f2f2f" stroke-width="1.2"/>')
    # Generalizacion entre actores: Cliente -> Anonimo, Administrador -> Cliente
    for (x1, y1), (x2, y2) in (((90, 244), (90, 160)), ((90, 419), (90, 345))):
        p.append(f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2 + 16}" stroke="#2f2f2f" stroke-width="1.3"/>')
        p.append(triangulo(x2, y2, "arriba"))
    # include: Reservar -> Calcular importe
    p.append('<line x1="562" y1="250" x2="655" y2="250" stroke="#2f2f2f" stroke-width="1.2" stroke-dasharray="6 4"/>')
    p.append('<polyline points="645,244 657,250 645,256" fill="none" stroke="#2f2f2f" stroke-width="1.3"/>')
    p.append(f'<text x="617" y="240" text-anchor="middle" font-size="11" {FUENTE}>«include»</text>')
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 940 565" width="940" height="565">\n' + "\n".join(p) + "\n</svg>"


SALIDA.mkdir(parents=True, exist_ok=True)
(SALIDA / "diagrama-clases.svg").write_text(diagrama_clases(), encoding="utf-8")
(SALIDA / "diagrama-casos-uso.svg").write_text(diagrama_casos_uso(), encoding="utf-8")
print("Diagramas generados en", SALIDA)
