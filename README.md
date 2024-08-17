# CyberBiology2
Мой проект искусственной жизни.

Подробнее тут: [Мои эксперименты с искусственной жизнью](https://habr.com/ru/articles/832862/)
(Версия - 1.9)

## Что скачивать

Версии на python не поддерживаются, и не добавлены во вкладку с релизами.

Сейчас основная версия - 2.0 все еще находится в разработке. Некоторые функции, такие как сохрнение мира, могут не работать. Советую скачивать версию 1.9, 1.10 или 1.11. Но для версий меньше 2.0 папки необходимо создавать вручную.

Советую скачивать через вкладку "releases"

## Интерфейс

Кнопка "Stop" просто включает/выключает паузу.

Кнопки "Predators", "Energy", "Minerals", "Age", "Color" и т.д. - меняют режим отрисовки.

Кнопки "Save" и "Show brain" позволяют сохранить выбранное существо или посмотреть его мозг соответственно. Для сохранения необходимо вписать название существа в строку для ввода ниже. Также необходимо наличие папки "saved objects".

Кнопки "Select", "Set" и "Remove" изменяют функцию мыши. Select позволяет выбирать существ (режим по умолчанию), Set - устанавливать загруженных (с версии 1.9.1), Remove - удалять. Чтобы сбросить выбранное существо, нужно в режиме "Select" нажать на пустое место.

Кнопка "Load bot" позволяет загрузить существо из файла(с версии 1.9.1). Для загрузки необходимо ввести имя файла в строке ввода выше.

Кнопки "Load world" и "Save world" позволяют загружать и сохранять мир соответственно(с версии 1.11, загрузка не работает в 2.0). Для сохранения или загрузки необходимо ввести имя файла в строке ввода выше, а также наличие папки "saved worlds".

Кнопка "New population" создает новую случайную популяцию.

"Kill all" соответственно, убивает всех ботов.

"Render" и "Record" переключают отрисовку и запись соответственно. Для записи требуется наличие папки "record", а в ней папок "color", "energy" и "predators"

## Изменения

### 1.1

-Добавлена органика

### 1.2

-Добавлена запись

-Добавлена команда сравнения своей энергии с энергией соседа (относительно и абсолютно)

### 1.3

-Частично исправлена ошибка с фантомными ботами

### 1.4

-Переходы в мозге теперь не увеличивают значение счетчика на n, а перемещают его сразу на индекс n

-Удалена запись

-Исправлена ошибка с фантомными ботами

### 1.5

-Добавлена возможность отключать отрисовку посредством нажатия F2

### 1.6

-Добавлены сохранение и загрузка миров посредством нажатия F3 и F4 соответственно

### 1.7

-Симуляция портирована на java

#### Python

-Добавлена запись посредством нажатия F5

### 1.8

#### Python

-Добавлена команда безусловного перехода

-На записи отображается количество шагов симуляции

### 1.9

#### Python

-Исправлена ошибка с невозможностью отличить врага от родственника

#### Java

-Добавлена команда безусловного перехода

### 1.9.1

-Добавлена возможность сохранять геном бота

### 1.10

-Добавлена мимикрия (при выполнении команды тратится 20 энергии, а бот становится родственником для всех ботов на 3 хода)

-Добавлены сенсоры количества энергии, минералов и возраста соседа

-Добавлена команда мутации

-Добавлена команда мутации соседа

-Добавлена команда атаки, отнимающая определенное кол - во энергии из параметра

-Шанс появления команд фотосинтеза и преобразования минералов в энергию увеличен в 2 раза

-Добавлен режим отрисовки мимикрии

-Органика теперь всегда падает

### 1.11

-Команда мимикрии теперь завершающая

-Добавлен сенсор направления

-Добавлена команда установки направления в случайное

-Добавлен случайный переход (шанс зависит от параметра)

### 2.0 pre-release 1

-Удалена мимикрия

-Временно удалена возможность загружать миры

-Количество энергии органики теперь отображается в режиме  отрисовки энергии

-Органику можно просматривать в режиме просмотра ботов

-Раз в 3 хода количество энергии органики уменьшается на 1. Если энергия кончится, органика умрет

-Улучшено отображение ботов в режиме отрисовки хищников

-Градиент режима отрисовки минералов изменен. Если у бота мало минералов - он голубой, если много - синий

-Градиент режима отрисовки возраста изменен. Если бот молодой - он желтый, если старый - синий

-Добавлен режим отрисовки кланов

-Добавлен режим отрисовки родственников (боты без мутаций одного цвета)

-Распознавание родственников теперь работает по цвету. Если значение каждого цветового канала соседа находится в диапазоне от -20 до 20 относительно значения бота, боты считаются родственниками

-Цвет теперь немного изменяется при делении, с шансом 1/100 меняется полностью. Цвет не зависит от мутаций

-Цвет изменяется и у потомка, и у предка

-При делении потомок мутирует с шансом 1/4

-Номера всех команд изменены

-Относительные команды объединены с абсолютными

-Удалена команда атаки, убивающая сразу

-Добавлена команда суицида

-Добавлена команда уменьшения возраста (за 2 энерргии)

-Добавлен сенсор зрения на 2 клетки

-Команды повернуть и сменить направление объединены

-Трата энергии при движении теперь 4 энергии

-Энергия теперь тратится за каждую выполненную команду

-Добавлена команда копирования цвета

-Добавлена команда изменения цвета(немного меняется)

-Добавлена команда изменения одного цветового канала(немного меняется)

-Добавлена команда смены цвета (полной)

-Добавлена команда смены одного цветового канала (полной)

-Добавлены многоклеточные цепочки

-Добавлена команда деления с добавлением соседа в цепочку

-Добавлено автоматическое создание папок "record", "saved objects" и "saved worlds"

-Добавлен сенсор положения бота в многоклеточной цепочке

-Добавлены прерывания. (Если бота укусили или отдали ему энергию, то сработает прерывание)

## Новости

07.08.24 12:24 Исправлена ошибка с сенсором позиции

07.08.24 13:07 Исправлена ошибка с добавлением потомка и в next, и в prev

07.08.24 13:09 Код версии 2.0 закомментирован

17.08.24 13:41 Добавлено больше прерываний(если не выполнились команды движения, атаки и т.д.). Удалено море(область в нижней части мира, в которой боты не могут получать минералы)
