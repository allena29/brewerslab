#!/usr/bin/python
from __future__ import division

import re
import sys

class jbkwater:

    def __init__(self):
        self.rxFloat=re.compile('^\d+\.?\d*$')

        self.InitCa = -1
        self.InitMg = -1
        self.InitNa = -1
        self.InitCO3 = -1
        self.InitSO4 = -1
        self.InitCl = -1

        self._reset()

    def _reset(self):
        self.Ca_ew = 20.039
        self.Mg_ew = 12.1525
        self.Na_ew = 22.989769
        self.CO3_ew = 30.00445
        self.SO4_ew = 48.0313
        self.Cl_ew = 35.453

        self.MagiNum = self.SO4_ew/self.Cl_ew
        self.gypsump_ew = 86.08558
        self.CaCl_di_ew = 73.50728
        self.Epsom_ew = 123.26
        self.salt_ew = 58.44
        self.glauber_eq = 89.04
        self.soda_ew = 143.1
        self.MgCO3_ew = 42.16
        self.CaCO3_ew = 50.04345
        self.HCO3_ew = 61.0168
    

    def MainBit(self):
        if self.InitCa < 0:
            raise ValueError('Calcium Not set')
        if self.InitMg < 0:
            raise ValueError('Magnesium Not Set')
        if self.InitNa < 0:
            raise ValueError('Sodium Not Set')
        if self.InitCO3 < 0:
            raise ValueError('CarbonateNot Set')
        if self.InitSO4 < 0:
            raise ValueError('Sulphate Not Set')
        if self.InitCl < 0:
            raise ValueError('Chloride Not Set')


    def setCa(self,val):
        if isinstance(val, float) and self.rxFloat.match( '%s' %(val)):
            self.InitCa = val
        else:
            raise ValueError('Invalid Calcium Value')

    def setMg(self,val):
        if isinstance(val, float) and self.rxFloat.match( '%s' %(val)):
            self.InitMg = val
        else:
            raise ValueError('Invalid Magnesium Value')

    def setNa(self,val):
        if isinstance(val, float) and self.rxFloat.match( '%s' %(val)):
            self.InitNa = val
        else:
            raise ValueError('Invalid Sodium Value')

    def setCO3(self,val):
        if isinstance(val, float) and self.rxFloat.match( '%s' %(val)):
            self.InitCO3 = val
        else:
            raise ValueError('Invalid Carbonate Value')

    def setSO4(self,val):
        if isinstance(val, float) and self.rxFloat.match( '%s' %(val)):
            self.InitSO4 = val
        else:
            raise ValueError('Invalid Sulphate Value')

    def setCl(self,val):
        if isinstance(val, float) and self.rxFloat.match( '%s' %(val)):
            self.InitCl = val
        else:
            raise ValueError('Invalid Chloride Value')


if __name__ == '__main__':
    water=jbkwater()
    water.setCa(97.0)
    water.setMg(7.4)
    water.setNa(40.40)
    water.setCO3(92.93)
    water.setSO4(78.7)
    water.setCl(73.0)
    water.MainBit()

