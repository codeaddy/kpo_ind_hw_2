## Конструирование программного обеспечения
## Сизикин Владислав, БПИ218
### Домашнее задание #2

Общая идея - программа строит граф по зависимостям, затем находит топологическую сортировку вершин (файлов), затем выводит содержимое самих файлов в этом порядке.

Важное замечание - зависимости в файлах должны располагаться на отдельных строках и строго в формате 'require {путь к файлу}'

В качестве корневого пути можно указывать как относительный (относительно папки проекта), так и абсолютный путь.

В папке test лежит пример, похожий на пример из условия
