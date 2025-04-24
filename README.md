# Final : Stock Market Simulation
---
## What is aims to do
This will be a simulation of 5 buyers with different habits and how they affect a stock price base on those habits. Furthermore certain 

## The UI
Here is the Main UI :\
![](Documents/MainUI.png)\
The UI has many different Parts to it : 

- ### The news
On the top of the UI will scroll from left ot right relevant events that happened to the Stock. it will say the news and how much it affected the stock itself.

- ### The Top Right
On the Top right we have 
- The Current Stock Price
- The Market Cap, bassicaly shows what the value of the entire stocks would be
- The market State, Crashing, Declining, Stable, Rising, or Booming 
- The Time, each tick is an hour and the UI, for stability, only adds a point after 10 ticks, so the time jumps by 10 hours each time 

- ### The Arrows 
The Arrows are used to manipulat the stock, you dont need to use them neither will you have to.

- ### Pause
Simple, pauses the market
- ### Hide Line
Hides/Show the Cyan line
- ### Hide/Show Candles 
Shows or hide the candle graph
- ### Keep History 
will control either we keep old data or we trim it, meaning disregard old data and only show the last 500h of data.
- ### Toggle Trading UI
The Trading UI is a UI given in order to trade in the simulated market.\
here's what it looks like :\
![](Documents/TradingUI.png)

- ### Toggle Stats UI
![](Documents/StatsUI.png)



## Design Patterns

### Stock market 