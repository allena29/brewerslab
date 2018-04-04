#!/usr/bin/env python2.7

import cmd2
import time
import random
import sys
import argparse
import json

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
        # print self.hidden_commands and we will be able to hide conf
        self.do_show = self._command_oper_show
        self.complete_show = self._autocomplete_oper_show

        self._in_conf_mode = False

        self._db_conf = {
                            'abc': {}
                         }

        oper_json = """
{
    "fermentation": {
        "lowpoint": "0",
        "monitor": false,
        "setpoint": "0",
        "probe": {
            "id": ""
        },
        "results": {
            "average": {
                "hourly": "0",
                "daily": "0",
                "minute": "0"
            },
            "latest": "0"
        },
        "highpoint": "0"
    },
    "ferhardware": {
        "probe": [
            {
                "id": "probe1"
            },
            {
                "id": "probe2",
                "offsets": [
                    {
                        "high": "2.00",
                        "low": "1.00",
                        "offset": "0"
                    }
                ]
            }
        ]
    }
}
	"""


        self._db_oper = json.loads(oper_json)

    def do_foo(self,ag):
        print 'do_foo'
    def _exit_conf_mode(self):
        self._in_conf_mode = False
        print ''
        self.prompt = 'robber@localhost> '

        del self.do_set
        del self.do_delete
        self.do_show = self._command_oper_show
        self.complete_show = self._autocomplete_oper_show

    def _enter_conf_mode(self):
        self._in_conf_mode = True
        self.prompt = 'robber@localhost% '
        print 'Entering configuration mode private'
        self._conf_header()
        # TODO in later version refresh these commands on tab complete.
        # TODO: make tab completion show a better column based presentation.
        self.do_set = self._command_set
        self.do_delete = self._command_delete
        self.do_show = self._command_conf_show
        self.complete_show = self._autocomplete_conf_show

    def _conf_header(self):
        print '[ok][%s]' % (time.ctime())
        print ''
        print '[edit]'

    # We use _command_xxxx prefix to show commands which will be dynamically removed
    # or added based on mode.
    def _autocomplete_oper_show(self, text, line, begidx, endidx):
        # we return a list, if the list contain one entry only that will trigger
        # completion.
        # Note the behaviour doesn't work with : separate lsts
        print 'line:%s' %(line)

        keys_to_navigate = line[5:].split(' ')
        print 'keystonaviage:%s' %(keys_to_navigate)
        our_node = self._db_oper

        for key in keys_to_navigate:
            if len(key) and our_node.has_key(key):
                print 'trying to reset our_node based on key',key
                our_node = our_node[key]

        print 'building completion based on'
        print our_node
        cmds = []
        for key in our_node:
            if key[0:len(text)] == text:
                cmds.append(key+ ' ')

#            cmds.append(key.replace(':', '_'))
        cmds.sort()
        return cmds


    def _command_delete(self, args):
        print 'command elete called', args

    def _command_set(self, args):
        'Set node in the configurationl database'
        if len(args) < 1:
            raise ValueError('Incomplete command: set %s' % (args))
        print 'command setw called', args

    def _command_oper_show(self, args):
        'Show node in the operational database'
        print 'command sshow called', args

    def _command_conf_show(self, args):
        'Show node in the configuration database'
        if len(args) < 1:
            raise ValueError('Incomplete command: show %s' % (args))
        print 'command sshow conf called', args



    def do_eof(self, args):
        # Implements CTRL+D
        if self._in_conf_mode:
            self._exit_conf_mode()
        else:
            print ''
            return self._STOP_AND_EXIT
        sys.exit(0)

    def do_exit(self, args):
        return self.do_eof(args)

    def do_configure(self, args):
        if self._in_conf_mode:
            raise ValueError('Already in configure mode')
        self._enter_conf_mode()

    # Ideally we would be able to tab complet confi... config... configure...
    def do_conf(self, args):
        self.do_configure(args)


if __name__ == '__main__':
    cli = Bandit()
    cli.cmdloop()
