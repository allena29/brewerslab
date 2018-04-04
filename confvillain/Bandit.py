#!/usr/bin/env python2.7

import cmd2
import time
import random
import sys
import argparse

from cmd2 import Cmd

class Bandit(Cmd):
   

    prompt = 'wild@localhost> '
 
    def __init__(self):    
        Cmd.__init__(self)

        self.allow_redirection = False

        # To remove built-in commands entirely, delete their "do_*" function from the 
        # cmd2.Cmd class
        del Cmd.do_load
        del Cmd.do_py
        del Cmd.do_pyscript
        del Cmd.do_shell
        del Cmd.do_alias
        del Cmd.do_unalias
        del Cmd.do_shortcuts
        del Cmd.do_edit
        del Cmd.do_set
        del Cmd.do_quit
        del Cmd.do__relative_load
        del Cmd.do_eof
        del Cmd.do_eos

        self.exclude_from_help.append('do_eof')
        self.exclude_from_help.append('do_conf')

        # this comes in 0.8 and will hide eof from tab completion :-)
        #print self.hidden_commands and we will be able to hide conf
        

        self._in_conf_mode = False

    def _exit_conf_mode(self):
        self._in_conf_mode = False
        print ''
        self.prompt = 'robber@localhost> '

        del self.do_set
        del self.do_delete

    def _enter_conf_mode(self):
        self._in_conf_mode = True
        self.prompt = 'robber@localhost% '
        print 'Entering configuration mode private'
        self._conf_header()
    
        self.do_set = self._command_set
        self.do_delete = self._command_delete

    def _conf_header(self):
        print '[ok][%s]' % (time.ctime())
        print ''
        print '[edit]'

    def _command_delete(self, args):
        print 'command elete called', args
    def _command_set(self, args):
        if len(args) < 1:
            raise ValueError('Incomplete command: set %s' %(args))
        print 'command set called',args

    def do_eof(self, args):
        if self._in_conf_mode:
            self._exit_conf_mode()
        else:
            return self._STOP_AND_EXIT
        sys.exit(0)

    def do_exit(self, args):
        return self.do_eof(args)

    def do_configure(self, args):
        if self._in_conf_mode:
            raise ValueError('Already in configure mode')
        self._enter_conf_mode()
        
    def do_conf(self, args):
        self.do_configure(args)


if __name__ == '__main__':
    cli = Bandit()
    cli.cmdloop()

