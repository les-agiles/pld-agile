# GeoFast - Manuel pour les utilisateurs

## 1. Importer un plan

![import_plan.png](ressource/import_plan.png)

Cette fonctionnalité permet de charger un plan à partir d’un fichier xml.

Après avoir cliquer sur le bouton, vous devez selectionner le fichier xml.

![selection_fichier_plan_xml.png](ressource/selection_fichier_plan_xml.png)

Le fichier xml doit être semblable à ce qui suit :

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<map>
<warehouse address="25303831"/>
<intersection id="25303831" latitude="45.74979" longitude="4.87572"/>
<intersection id="342873532" latitude="45.76051" longitude="4.8783274"/>
<intersection id="208769499" latitude="45.760597" longitude="4.87622"/>
<intersection id="975886496" latitude="45.756874" longitude="4.8574047"/>
<segment destination="975886496" length="51.028988" name="Impasse Lafontaine" origin="342873532"/>
<segment destination="342873532" length="51.028988" name="Impasse Lafontaine" origin="975886496"/>
<segment destination="208769499" length="106.73056" name="Rue Frédéric Passy" origin="208769499"/>
<segment destination="208769499" length="96.57731" name="Rue Édouard Aynard" origin="975886496"/>
<segment destination="342873532" length="64.89446" name="Rue Feuillat" origin="25303831"/>
<segment destination="208769499" length="153.72511" name="Rue Feuillat" origin="25303831"/>
<segment destination="975886496" length="122.619156" name="Rue Bara" origin="25303831"/>
<segment destination="25303831" length="118.890465" name="Avenue Lacassagne" origin="975886496"/>
</map>
```
Un fois le plan chargé, les points d'intersections sont visibles en gris sur la carte.

![chargement_plan.png](ressource/chargement_plan.png)

## 2. Importer un programme de livraison

![import_programme.png](ressource/import_programme.png)

Cette fonctionnalité permet de charger un programme de livraison à partir d’un fichier xml.
Au préalable, vous devez avoir importé un plan (voir partie 1)

Après avoir cliqué sur le bouton, vous devez selectionner le fichier xml.

![selection_fichier_programme_xml.png](ressource/selection_fichier_programme_xml.png)

Le fichier xml doit être semblable à ce qui suit :

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<planningRequest>
  <request courier="1" deliveryAddress="342873532" deliveryDuration="600" deliveryTime="9"/>
  <request courier="2" deliveryAddress="208769499" deliveryDuration="480" deliveryTime="9"/>
  <request courier="1" deliveryAddress="208769499" deliveryDuration="600" deliveryTime="8"/>
  <request courier="2" deliveryAddress="975886496" deliveryDuration="480" deliveryTime="10"/>
</planningRequest>
```
les "deliveryAddress" devant correspondre au "id" des intersections du plan (voir partie 1).

Un fois le programme chargé, les points de livraison de chaque livreur sont visibles sur la carte et sur la partie droite de l'interface.

[IMAGE]

