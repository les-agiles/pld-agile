Documentation technique de GitHub Actions
====================================

GitHub Actions est un service d'intégration continue et de déploiement continu (CI/CD) fourni par GitHub. Il permet d'automatiser divers processus liés au développement logiciel, tels que les tests, la construction, le déploiement et d'autres tâches liées au flux de travail de développement.

### Source dépendance

<https://github.com/les-agiles/pld-agile/actions>

### Fonctionnement

GitHub Actions fonctionne en utilisant des fichiers de configuration appelés "workflows" qui décrivent les actions à exécuter en réponse à certains événements sur GitHub, tels que les push de code, les pull requests, les créations de tag, etc. Ces workflows sont définis dans des fichiers YAML stockés dans le répertoire .github/workflows de votre référentiel GitHub.

Chaque workflow est composé de "jobs" qui représentent des unités de travail indépendantes. Chaque job peut contenir une ou plusieurs "steps" qui décrivent des actions individuelles à effectuer, telles que la vérification du code, la construction de l'application, les tests unitaires, le déploiement, etc.

Les workflows peuvent être déclenchés automatiquement par des événements spécifiques sur GitHub, ou manuellement par les développeurs. Une fois déclenchés, GitHub Actions exécute les actions spécifiées dans les workflows dans un environnement d'exécution isolé, tel qu'un conteneur Docker, en fournissant des résultats détaillés et des journaux d'exécution pour chaque étape du processus.

### Utilité

#### Automatisation des tâches répétitives

GitHub Actions permet d'automatiser les tâches répétitives du cycle de développement logiciel, telles que la construction, les tests, la validation du code, la génération de rapports, etc. Cela permet aux développeurs de se concentrer sur la création de code plutôt que sur les tâches manuelles fastidieuses.

#### Intégration continue

GitHub Actions facilite l'intégration continue en exécutant automatiquement des tests et des validations à chaque modification du code source sur GitHub. Cela garantit que les nouvelles fonctionnalités et les modifications apportées au code sont testées et validées rapidement, réduisant ainsi le risque d'introduire des bogues dans le code.
