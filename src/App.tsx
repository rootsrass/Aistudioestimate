/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { motion } from "motion/react";
import { Layout, FileCode, Smartphone, CheckCircle2, History, Settings } from "lucide-react";

export default function App() {
  return (
    <div className="min-h-screen bg-neutral-50 text-neutral-900 font-sans selection:bg-indigo-100">
      {/* Header */}
      <header className="border-b border-neutral-200 bg-white/80 backdrop-blur-md sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
              <Layout className="w-5 h-5 text-white" />
            </div>
            <span className="font-semibold text-lg tracking-tight">Contractor Estimate Pro</span>
          </div>
          <div className="flex items-center gap-4">
            <span className="text-xs font-mono px-2 py-1 bg-neutral-100 rounded text-neutral-500 uppercase tracking-wider">Android Project Mode</span>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-12">
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="grid grid-cols-1 lg:grid-cols-12 gap-12"
        >
          {/* Status Column */}
          <div className="lg:col-span-4 space-y-8">
            <div>
              <h2 className="text-sm font-bold uppercase tracking-widest text-indigo-600 mb-4">Phase 9: Scalability</h2>
              <h1 className="text-4xl font-medium tracking-tight text-neutral-900 leading-[1.1]">
                Service Plugin <br/> Architecture
              </h1>
              <p className="mt-4 text-neutral-600 leading-relaxed">
                The core engine has been refactored into a registry-based strategy pattern. This allows adding new services (Pavers, Irrigation) by simply registering a new calculator, without touching core logic.
              </p>
            </div>

            <div className="space-y-3">
              <StatusItem label="Strategy Pattern (ServiceCalculator)" completed />
              <StatusItem label="ServiceRegistry Implementation" completed />
              <StatusItem label="Dagger Multibindings-Ready DI" completed />
              <StatusItem label="Decoupled Domain/UI Logic" completed />
              <StatusItem label="Production Hardening (Next)" />
            </div>
          </div>

            <div className="mt-8 p-6 bg-neutral-900 rounded-xl text-neutral-100 shadow-xl overflow-hidden relative group">
              <div className="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition-opacity">
                <Smartphone className="w-24 h-24 rotate-12" />
              </div>
              <h3 className="text-lg font-bold mb-4 flex items-center gap-2 text-indigo-400">
                <Smartphone className="w-5 h-5" />
                Mobile APK Workflow (No PC Needed)
              </h3>
              <ol className="space-y-4 text-sm relative z-10">
                <li className="flex gap-3">
                  <span className="font-mono text-indigo-400">01</span>
                  <span>Tap **Settings (Gear Icon)** at the top left of this screen.</span>
                </li>
                <li className="flex gap-3">
                  <span className="font-mono text-indigo-400">02</span>
                  <span>Select **Export to GitHub** to push this project to your repo.</span>
                </li>
                <li className="flex gap-3">
                  <span className="font-mono text-indigo-400">03</span>
                  <span>Open **GitHub.com** on your phone, go to the **Actions** tab. If it fails, ensure `gradlew` has execute permissions.</span>
                </li>
                <li className="flex gap-3">
                  <span className="font-mono text-indigo-400">04</span>
                  <span>Once the build finishes (~5m), tap the Run &rarr; scroll to **Artifacts** &rarr; download **app-debug.apk**. (Build configured to use system Gradle for reliability).</span>
                </li>
                <li className="flex gap-3">
                  <span className="font-mono text-indigo-400">05</span>
                  <span>Install the APK on your phone (Enable "Install from Unknown Sources" if prompted).</span>
                </li>
              </ol>
            </div>
        </motion.div>
      </main>

      {/* Footer */}
      <footer className="mt-24 border-t border-neutral-200 py-12 px-6">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-6">
          <p className="text-sm text-neutral-500 italic font-serif">
            Designed for durability. Built for field precision.
          </p>
          <div className="flex items-center gap-4 text-xs font-mono uppercase tracking-widest text-neutral-400">
            <span>Powered by Gemini & Antigravity</span>
          </div>
        </div>
      </footer>
    </div>
  );
}

function StatusItem({ label, completed = false }: { label: string; completed?: boolean }) {
  return (
    <div className="flex items-center gap-3">
      <div className={`w-5 h-5 rounded-full flex items-center justify-center border ${completed ? "bg-indigo-600 border-indigo-600 text-white" : "border-neutral-300 text-transparent"}`}>
        <CheckCircle2 className="w-3 h-3" />
      </div>
      <span className={`text-sm ${completed ? "text-neutral-900 font-medium" : "text-neutral-400"}`}>{label}</span>
    </div>
  );
}

function FileLink({ name }: { name: string }) {
  return (
    <li className="flex items-center gap-3 text-neutral-600 group cursor-default">
      <div className="w-1.5 h-1.5 rounded-full bg-neutral-300 group-hover:bg-indigo-400 transition-colors" />
      <span className="text-sm font-mono group-hover:text-indigo-600 transition-colors">{name}</span>
    </li>
  );
}

function NavStep({ icon, label, active = false }: { icon: React.ReactNode; label: string; active?: boolean }) {
  return (
    <div className={`flex items-center gap-3 p-2 rounded-lg ${active ? "bg-white shadow-sm ring-1 ring-neutral-200" : ""}`}>
      <div className={`${active ? "text-indigo-600" : "text-neutral-400"}`}>
        {icon}
      </div>
      <span className={`text-xs ${active ? "font-bold text-neutral-900" : "text-neutral-500"}`}>{label}</span>
    </div>
  );
}
