# -*- coding: utf-8 -*-
"""
Created on Fri Nov 11 09:35:29 2022

@author: Bruno M. Breggia
"""

import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

filename= "C:/Users/UNER/git/AgentBasedTuberculosis_Repast/jzombies/ModelOutput.2022.nov..11.08_34_54.txt"
df = pd.read_csv(filename)

plt.plot(df["tick"], df["Susceptible Count"], label="Susceptibles")
plt.plot(df["tick"], df["Infected Count"], label="Infectados")

plt.xlabel("Ticks")
plt.ylabel("Cantidad de individuos")
plt.grid(True)
plt.legend()
