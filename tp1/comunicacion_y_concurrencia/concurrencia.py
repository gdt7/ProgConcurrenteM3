from multiprocessing import Pipe
import multiprocessing
import sys

SUCURSAL_IDX = 0
FECHA_IDX = 1
PRODUCTO_IDX = 2
MONTO_IDX = 3

def get_mas_ocurrencias(datos,tipo,unidad):
    mayores_ocurrencias = []
    mayor = 0
    for key,value in datos.items():
        if(value > mayor):
            mayor = value
            del mayores_ocurrencias [:]
            mayores_ocurrencias.append([key,value])
        elif(value == mayor):
            mayores_ocurrencias.append([key,value])


    if(len(mayores_ocurrencias) >= 3):
            counter = 0
            for data in mayores_ocurrencias:
                print(f"{tipo}: {data[0]} | {data[1]} {unidad}")
                counter += 1
                if(counter == 3):
                    break
    else:
        for data in mayores_ocurrencias:
            print(f"{tipo}: {data[0]} | {data[1]} {unidad}")

def procesar_ventas(archivo_ventas):
    with open(archivo_ventas) as arch:
        next(arch)
        registros_sucursal1 = []
        registros_sucursal2 = []
        for registro in arch:
            registro = registro.split(",")
            if(int(registro[SUCURSAL_IDX]) == 1):
                registros_sucursal1.append(registro)
            else:
                registros_sucursal2.append(registro)

    return registros_sucursal1,registros_sucursal2

def mostrar_resultados(num_sucursal,monto_total,producto_mas_vendido,fecha_mas_ventas,fechas_mas_monto_vendido):

    print(f"\nSucursal N°{num_sucursal}")
    print(f"El monto total de ventas de la sucursal {num_sucursal} es: ${monto_total}")

    print("------ Producto/s mas vendido/s: ------")
    get_mas_ocurrencias(producto_mas_vendido,"Producto", "unidades")

    print("------ Fechas con más ventas ------")
    get_mas_ocurrencias(fecha_mas_ventas,"Fecha","ventas")

    print("------ Fechas con mayor importe de ventas ------")
    get_mas_ocurrencias(fechas_mas_monto_vendido,"Fecha","pesos")

def sucursal1_handler(conn_recv, conn_env):
    num_sucursal = 1
    monto_total = 0
    producto_mas_vendido = {}
    fecha_mas_ventas = {}
    fechas_mas_monto_vendido = {}

    conn_env.close()
    ventas = conn_recv.recv()
    conn_recv.close()

    for venta in ventas:
        monto_total += int(venta[MONTO_IDX])

        if(venta[PRODUCTO_IDX] in producto_mas_vendido):
            producto_mas_vendido[venta[PRODUCTO_IDX]] += 1
        else:
            producto_mas_vendido[venta[PRODUCTO_IDX]] = 1

        if(venta[FECHA_IDX] in fecha_mas_ventas):
            fecha_mas_ventas[venta[FECHA_IDX]] += 1
        else:
            fecha_mas_ventas[venta[FECHA_IDX]] = 1

        if(venta[FECHA_IDX] in fechas_mas_monto_vendido):
            fechas_mas_monto_vendido[venta[FECHA_IDX]] += float(venta[MONTO_IDX])
        else:
            fechas_mas_monto_vendido[venta[FECHA_IDX]] = float(venta[MONTO_IDX])

    print(f"\nSucursal N°{num_sucursal}")
    print(f"El monto total de ventas de la sucursal {num_sucursal} es: ${monto_total}")

    print("------ Producto/s mas vendido/s: ------")
    get_mas_ocurrencias(producto_mas_vendido,"Producto", "unidades")

    print("------ Fechas con más ventas ------")
    get_mas_ocurrencias(fecha_mas_ventas,"Fecha","ventas")

    print("------ Fechas con mayor importe de ventas ------")
    get_mas_ocurrencias(fechas_mas_monto_vendido,"Fecha","pesos")

    sys.exit(0)

def sucursal2_handler(conn_recv, conn_env):
    num_sucursal = 2
    monto_total = 0
    producto_mas_vendido = {}
    fecha_mas_ventas = {}
    fechas_mas_monto_vendido = {}

    conn_env.close()
    ventas = conn_recv.recv()
    conn_recv.close()

    for venta in ventas:
        monto_total += int(venta[MONTO_IDX])

        if(venta[PRODUCTO_IDX] in producto_mas_vendido):
            producto_mas_vendido[venta[PRODUCTO_IDX]] += 1
        else:
            producto_mas_vendido[venta[PRODUCTO_IDX]] = 1

        if(venta[FECHA_IDX] in fecha_mas_ventas):
            fecha_mas_ventas[venta[FECHA_IDX]] += 1
        else:
            fecha_mas_ventas[venta[FECHA_IDX]] = 1

        if(venta[FECHA_IDX] in fechas_mas_monto_vendido):
            fechas_mas_monto_vendido[venta[FECHA_IDX]] += int(venta[MONTO_IDX])
        else:
            fechas_mas_monto_vendido[venta[FECHA_IDX]] = int(venta[MONTO_IDX])

    print(f"\nSucursal N°{num_sucursal}")
    print(f"El monto total de ventas de la sucursal {num_sucursal} es: ${monto_total}")

    print("------ Producto/s mas vendido/s: ------")
    get_mas_ocurrencias(producto_mas_vendido,"Producto", "unidades")

    print("------ Fechas con más ventas ------")
    get_mas_ocurrencias(fecha_mas_ventas,"Fecha","ventas")

    print("------ Fechas con mayor importe de ventas ------")
    get_mas_ocurrencias(fechas_mas_monto_vendido,"Fecha","pesos")

    sys.exit(0)

def main():
    archivo_ventas = sys.argv[1]

    recepcion_sucursal1, envio_sucursal1 = Pipe(False)
    recepcion_sucursal2, envio_sucursal2 = Pipe(False)

    p1 = multiprocessing.Process(target=sucursal1_handler, args=(recepcion_sucursal1,envio_sucursal1))
    p2 = multiprocessing.Process(target=sucursal2_handler, args=(recepcion_sucursal2,envio_sucursal2))
    p1.start()
    p2.start()

    registros_sucursal1, registros_sucursal2 = procesar_ventas(archivo_ventas)

    recepcion_sucursal1.close()
    envio_sucursal1.send(registros_sucursal1)
    envio_sucursal1.close()

    recepcion_sucursal2.close()
    envio_sucursal2.send(registros_sucursal2)
    envio_sucursal2.close()

    p1.join()
    p2.join()

if __name__=="__main__":
    main()
