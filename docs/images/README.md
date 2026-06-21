# Как сделать скриншоты GitHub Insights

По методичке нужны скриншоты с GitHub Insights для раздела "Git-статистика".

## Что нужно сделать:

### 1. Commit Activity (График коммитов)
1. Откройте https://github.com/ваш-username/CarRentalApp
2. Нажмите **Insights** (вкладка сверху)
3. В левом меню выберите **Contributors**
4. Сделайте скриншот всей страницы (график коммитов по неделям)
5. Сохраните как `docs/images/git-commit-activity.png`

### 2. Punch Card (Тепловая карта)
1. На странице Insights → **Contributors**
2. Прокрутите ниже — там будет **Punch Card** (часы × дни недели)
3. Сделайте скриншот
4. Сохраните как `docs/images/git-punch-card.png`

### 3. Языки программирования
1. На главной странице репозитория
2. Справа вверху найдите блок **Languages**
3. Сделайте скриншот
4. Сохраните как `docs/images/git-languages.png`

### 4. Pulse (Активность за неделю)
1. Нажмите **Insights** → **Pulse** (верхнее меню)
2. Сделайте скриншот
3. Сохраните как `docs/images/git-pulse.png`

## Как вставить в README.md:

```markdown
## Git-статистика

- Коммитов: 47
- Период: 01.03.2026 — 30.05.2026
- Коммитов в день: 2.9

### Commit Activity
![](images/git-commit-activity.png)

### Punch Card
![](images/git-punch-card.png)

### Языки
![](images/git-languages.png)
```

## Требования методички:
- Раздел "Git-статистика" в README.md
- Минимум 2 скриншота: Commit Activity + Punch Card
- Ссылки вида `![](images/file.png)`
- +2% к итоговой оценке
